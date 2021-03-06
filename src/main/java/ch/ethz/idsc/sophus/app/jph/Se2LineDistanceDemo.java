// code by jph
package ch.ethz.idsc.sophus.app.jph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.JToggleButton;

import ch.ethz.idsc.owl.gui.GraphicsUtil;
import ch.ethz.idsc.owl.gui.ren.AxesRender;
import ch.ethz.idsc.owl.gui.win.GeometricLayer;
import ch.ethz.idsc.sophus.app.api.ControlPointsDemo;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplay;
import ch.ethz.idsc.sophus.app.api.GeodesicDisplays;
import ch.ethz.idsc.sophus.crv.LieGroupLineDistance;
import ch.ethz.idsc.sophus.crv.LieGroupLineDistance.NormImpl;
import ch.ethz.idsc.sophus.lie.LieExponential;
import ch.ethz.idsc.sophus.lie.LieGroup;
import ch.ethz.idsc.sophus.lie.se2c.Se2CoveringExponential;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.sophus.ply.Arrowhead;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Subdivide;
import ch.ethz.idsc.tensor.img.ColorDataIndexed;
import ch.ethz.idsc.tensor.img.ColorDataLists;
import ch.ethz.idsc.tensor.opt.ScalarTensorFunction;
import ch.ethz.idsc.tensor.sca.Round;

/* package */ class Se2LineDistanceDemo extends ControlPointsDemo {
  private static final ColorDataIndexed COLOR_DATA_INDEXED_DRAW = ColorDataLists._097.cyclic().deriveWithAlpha(192);
  private static final ColorDataIndexed COLOR_DATA_INDEXED_FILL = ColorDataLists._097.cyclic().deriveWithAlpha(182);
  // ---
  private final JToggleButton axes = new JToggleButton("axes");

  public Se2LineDistanceDemo() {
    super(false, GeodesicDisplays.SE2_ONLY);
    timerFrame.jToolBar.add(axes);
    Tensor tensor = Tensors.fromString("{{0, 0, 0}, {5, 0, 1}}");
    setControlPointsSe2(tensor);
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (axes.isSelected())
      AxesRender.INSTANCE.render(geometricLayer, graphics);
    GraphicsUtil.setQualityHigh(graphics);
    renderControlPoints(geometricLayer, graphics);
    Tensor sequence = getControlPointsSe2();
    if (sequence.length() == 2) {
      GeodesicDisplay geodesicDisplay = geodesicDisplay();
      LieGroup lieGroup = geodesicDisplay.lieGroup();
      LieExponential lieExponential = geodesicDisplay.lieExponential();
      // ---
      GeodesicInterface geodesicInterface = geodesicDisplay.geodesicInterface();
      Tensor beg = sequence.get(0);
      Tensor end = sequence.get(1);
      ScalarTensorFunction curve = geodesicInterface.curve(beg, end);
      {
        Tensor tensor = Subdivide.of(-0.5, 1.5, 55).map(curve);
        Path2D path2d = geometricLayer.toPath2D(Tensor.of(tensor.stream().map(geodesicDisplay::toPoint)));
        graphics.setColor(Color.BLUE);
        graphics.draw(path2d);
      }
      {
        LieGroupLineDistance lieGroupLineDistance = //
            new LieGroupLineDistance(lieGroup, Se2CoveringExponential.INSTANCE::log);
        NormImpl normImpl = lieGroupLineDistance.tensorNorm(beg, end);
        Tensor mouse = geometricLayer.getMouseSe2State();
        {
          Tensor project = normImpl.project(mouse);
          Tensor exp = lieExponential.exp(project);
          Tensor glb = lieGroup.element(beg).combine(exp);
          {
            geometricLayer.pushMatrix(geodesicDisplay.matrixLift(glb));
            Path2D path2d = geometricLayer.toPath2D(Arrowhead.of(0.4));
            path2d.closePath();
            graphics.setColor(COLOR_DATA_INDEXED_FILL.getColor(2));
            graphics.fill(path2d);
            graphics.setColor(COLOR_DATA_INDEXED_DRAW.getColor(2));
            graphics.draw(path2d);
            geometricLayer.popMatrix();
          }
        }
        {
          Tensor orthogonal = normImpl.orthogonal(mouse);
          Tensor exp = lieExponential.exp(orthogonal);
          Tensor glb = lieGroup.element(beg).combine(exp);
          {
            graphics.setColor(Color.DARK_GRAY);
            graphics.setFont(new Font(Font.DIALOG, Font.PLAIN, 12));
            graphics.drawString("" + orthogonal.map(Round._3), 0, 40);
          }
          {
            geometricLayer.pushMatrix(geodesicDisplay.matrixLift(glb));
            Path2D path2d = geometricLayer.toPath2D(Arrowhead.of(0.4));
            path2d.closePath();
            graphics.setColor(COLOR_DATA_INDEXED_FILL.getColor(3));
            graphics.fill(path2d);
            graphics.setColor(COLOR_DATA_INDEXED_DRAW.getColor(3));
            graphics.draw(path2d);
            geometricLayer.popMatrix();
          }
        }
      }
      // ---
      {
        geometricLayer.pushMatrix(geodesicDisplay.matrixLift(curve.apply(RationalScalar.HALF)));
        Path2D path2d = geometricLayer.toPath2D(Arrowhead.of(0.5));
        path2d.closePath();
        graphics.setColor(COLOR_DATA_INDEXED_FILL.getColor(0));
        graphics.fill(path2d);
        graphics.setColor(COLOR_DATA_INDEXED_DRAW.getColor(0));
        graphics.draw(path2d);
        geometricLayer.popMatrix();
      }
    }
  }

  public static void main(String[] args) {
    new Se2LineDistanceDemo().setVisible(1200, 600);
  }
}
