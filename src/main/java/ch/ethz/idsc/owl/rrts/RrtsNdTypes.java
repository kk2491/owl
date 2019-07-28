// code by gjoel
package ch.ethz.idsc.owl.rrts;

import ch.ethz.idsc.owl.data.nd.NdCenterInterface;
import ch.ethz.idsc.sophus.math.Extract2D;
import ch.ethz.idsc.tensor.Tensor;

/* package */ enum RrtsNdTypes implements RrtsNdType {
  // RN {
  // @Override
  // public NdCenterInterface getNdCenterInterface(Tensor tensor) {
  // return NdCenterInterface.euclidean(tensor);
  // }
  // },
  SE2_EUCLIDEAN {
    @Override
    public Tensor convert(Tensor tensor) {
      return Extract2D.FUNCTION.apply(tensor);
    }

    @Override
    public NdCenterInterface getNdCenterInterface(Tensor tensor) {
      return NdCenterInterface.euclidean(convert(tensor));
    }
  },
  SE2_CLOTHOID {
    @Override
    public NdCenterInterface getNdCenterInterface(Tensor tensor) {
      return new ClothoidNdCenter(tensor);
    }
  };
  @Override
  public Tensor convert(Tensor tensor) {
    return tensor;
  }
}
