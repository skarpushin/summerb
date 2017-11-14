package org.summerb.utils.exceptions.dto;

import java.io.Serializable;

import org.summerb.approaches.jdbccrud.common.DtoBase;
import org.summerb.approaches.security.api.dto.NotAuthorizedResult;
import org.summerb.approaches.security.api.exceptions.NotAuthorizedException;
import org.summerb.approaches.validation.FieldValidationException;
import org.summerb.approaches.validation.ValidationErrors;

/**
 * Exception might want to implement this interface if there is an object that
 * describes the exception. It has to be something that {@link Serializable} as
 * intention is to send it over network
 * 
 * I.e. for {@link FieldValidationException} it's {@link ValidationErrors}. For
 * {@link NotAuthorizedException} it's {@link NotAuthorizedResult}..
 * 
 * @author sergeyk
 *
 */
public interface HasErrorDescriptionObject<T extends DtoBase> {
	T getErrorDescriptionObject();
}
