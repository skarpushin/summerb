package org.summerb.approaches.jdbccrud.api.dto;

import org.summerb.approaches.jdbccrud.api.EasyCrudService;

/**
 * Impl this interface if your DTO's primary key is GUID.
 * {@link EasyCrudService} will generate new guid for PK if not specified
 * 
 * @author sergey.karpushin
 *
 */
public interface HasUuid extends HasId<String> {

}
