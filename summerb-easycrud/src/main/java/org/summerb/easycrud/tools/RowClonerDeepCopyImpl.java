package org.summerb.easycrud.tools;

import java.io.NotSerializableException;
import org.summerb.utils.objectcopy.Clonnable;
import org.summerb.utils.objectcopy.DeepCopy;

public class RowClonerDeepCopyImpl implements RowCloner {
  @SuppressWarnings("unchecked")
  @Override
  public Object clone(Object row) {
    try {
      if (row instanceof Clonnable) {
        return ((Clonnable<Object>) row).clone();
      }
      return DeepCopy.copyOrPopagateExcIfAny(row);
    } catch (NotSerializableException nse) {
      throw new RuntimeException(
          "Some files are not serializable. Consider implementing Clonnable interface", nse);
    } catch (Throwable t) {
      throw new RuntimeException("Failed to clone row", t);
    }
  }
}
