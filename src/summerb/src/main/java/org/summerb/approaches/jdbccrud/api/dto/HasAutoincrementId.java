package org.summerb.approaches.jdbccrud.api.dto;

import org.summerb.approaches.jdbccrud.api.EasyCrudService;

/**
 * Impl this interface to indicate this DTO has auto-increment id.
 * 
 * {@link EasyCrudService} then will return a PK of a newly created row
 * 
 * @author sergey.karpushin
 *
 */
public interface HasAutoincrementId extends HasId<Long> {

}
