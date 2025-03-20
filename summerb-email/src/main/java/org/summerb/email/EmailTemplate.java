package org.summerb.email;

import org.summerb.email.dto.Email;
import org.summerb.email.dto.EmailParameters;

/**
 * This interface is used to build instances of an email based on a particular template.
 *
 * @author Sergey Karpushin
 */
public interface EmailTemplate {

  /**
   * build actual message from parameters object
   *
   * @param emailParameters parameters object for building instance of the email from this template
   * @return {@link Email} which ready to be send into transport
   */
  Email apply(EmailParameters emailParameters);
}
