package org.summerb.approaches.jdbccrud.api.dto;

import org.summerb.approaches.jdbccrud.api.StringIdGenerator;
import org.summerb.approaches.jdbccrud.impl.EasyCrudDaoMySqlImpl;

/**
 * Impl this interface if your DTO's primary key is automatically generated
 * String.
 * 
 * Name of this interface remains HasUuid for compatibility purposes, while
 * actually you can customize underlying impl and provide string of any format
 * by setting instance of {@link StringIdGenerator} to
 * {@link EasyCrudDaoMySqlImpl#setStringIdGenerator(StringIdGenerator)}
 * 
 * @author sergey.karpushin
 *
 */
public interface HasUuid extends HasId<String> {

}
