package org.summerb.approaches.jdbccrud.common;

import java.io.Serializable;

import org.summerb.approaches.jdbccrud.api.dto.EntityChangedEvent;
import org.summerb.approaches.jdbccrud.api.dto.tools.EntityChangedEventAdapter;

/**
 * Special interface used to mark your DTOs.
 * 
 * It might help to correctly handle JSON serialization and it's also required
 * by other parts of the library, like {@link EntityChangedEvent}
 * 
 * It's also useful for enforcing which classes are allowed to be deserialized
 * when client determines which class to use when deserializing DTO. I.e. see
 * {@link EntityChangedEventAdapter}
 * 
 * @author sergeyk
 *
 */
public interface DtoBase extends Serializable {

}
