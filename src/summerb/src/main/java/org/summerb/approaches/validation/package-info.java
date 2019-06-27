/**
 * This package contains core design and impl for validation of input/data. It
 * is primary strong side compared to other approaches is super easy to use and
 * also transparency it gives -- validation related to particular entity is
 * contained in 1 place and easily readable and augmentable.
 * 
 * It doesn't operate with human-readble text, instead it operates upon message
 * codes (see {@link org.summerb.approaches.i18n.HasMessageCode}.
 * 
 * Also, it supports validation of hierarchical structures.
 * 
 * Start with an empty instance of
 * {@link org.summerb.approaches.validation.ValidationContext}, call out-of the
 * box methods or add validation errors manually using
 * {@link org.summerb.approaches.validation.ValidationContext#add(ValidationError)}.
 * And then call
 * {@link org.summerb.approaches.validation.ValidationContext#throwIfHasErrors()}
 * to throw well-structured exception containing all errors collected.
 * 
 * In case needed to return to REST client, use
 * {@link org.summerb.approaches.validation.FieldValidationException#getErrorDescriptionObject()}
 * 
 * @author sergeyk
 *
 */
package org.summerb.approaches.validation;