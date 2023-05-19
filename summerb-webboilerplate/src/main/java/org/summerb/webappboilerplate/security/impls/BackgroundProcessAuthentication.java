package org.summerb.webappboilerplate.security.impls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.summerb.spring.security.SecurityConstants;
import org.summerb.users.api.dto.User;

import com.google.common.base.Preconditions;

public class BackgroundProcessAuthentication<TUserDetails extends UserDetails, TUser extends User>
    implements Authentication {
  protected static final long serialVersionUID = 3710514197842955814L;

  public static List<String> AUTHORITIES_STRINGS =
      new ArrayList<>(Arrays.asList(SecurityConstants.ROLE_BACKGROUND_PROCESS));
  public static List<? extends GrantedAuthority> AUTHORITIES =
      AUTHORITIES_STRINGS.stream()
          .map(x -> new SimpleGrantedAuthority(x))
          .collect(Collectors.toList());

  protected final String origin;
  protected TUserDetails userDetails;
  protected final Collection<? extends GrantedAuthority> authorities;

  protected Function<TUserDetails, TUser> userGetter;

  /**
   * @param origin some string which is probably suppose to clarify what is the origin of that
   *     authentication. Not used for any logic - just for tracing/debugging purposes
   * @param userDetails user details
   * @param userGetter a getter that can get {@link User} from {@link UserDetails}
   */
  public BackgroundProcessAuthentication(
      String origin,
      TUserDetails userDetails,
      Function<TUserDetails, TUser> userGetter,
      Collection<? extends GrantedAuthority> authorities) {
    Preconditions.checkArgument(userDetails != null, "userDetails required");
    Preconditions.checkArgument(userGetter != null, "userGetter required");

    this.origin = origin;
    this.userDetails = userDetails;
    this.userGetter = userGetter;
    this.authorities = authorities;
  }

  public TUser getUser() {
    return userGetter.apply(userDetails);
  }

  @Override
  public String getName() {
    return "Background process (" + origin + ")";
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public Object getCredentials() {
    return "no password";
  }

  @Override
  public Object getDetails() {
    return null;
  }

  @Override
  public Object getPrincipal() {
    return userDetails;
  }

  @Override
  public boolean isAuthenticated() {
    return true;
  }

  @Override
  public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
    throw new IllegalStateException("Opearion is not supported");
  }
}
