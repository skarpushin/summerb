package org.summerb.approaches.springmvc.security.apis;

import org.summerb.approaches.validation.FieldValidationException;

public interface LoginEligibilityVerifier {

	void validateUserAllowedToLogin(String username) throws FieldValidationException;

}