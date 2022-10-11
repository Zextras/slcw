package com.zextras;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchScope;
import com.zextras.conection.configs.LdapConnectionConfig;
import com.zextras.conection.factories.LdapConnectionFactory;
import com.zextras.operations.results.OperationResult;
import com.zextras.persistence.Filter;
import com.zextras.persistence.SlcwException;
import com.zextras.slcwBeans.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SlcwClientIT extends LdapIT {

  private SlcwClient client;

  @BeforeEach
  public void setUp() {
    LDAPConnection connection = new LdapConnectionFactory(
        LdapConnectionConfig.builder()
            .host(container.getHost())
            .port(container.getMappedPort(389))
            .bindDN("uid=zimbra,cn=admins,cn=zimbra")
            .password("password")
            .build())
        .openConnection();
    client = new SlcwClient(connection, "dc=mail,dc=local");
  }

  @Test
  public void shouldGetById() {
    final User expectedUser = new User("inetOrgPerson", "Name", "Surname", 6785949);
    client.add(expectedUser);
    final User actualUser = client.getById("Name Surname", User.class);
    assertEquals(expectedUser, actualUser);
  }

  @Test
  public void shouldReturnSuccessOnAddOperation() {
    final User user = new User("inetOrgPerson", "Name", "Surname", 6785949);
    final OperationResult result = client.add(user);
    assertEquals("0 (success)", result.toString());
  }

  @Test
  public void shouldReturnSuccessOnDeleteOperation() {
    final User user = new User("inetOrgPerson", "Name", "Surname", 6785949);
    client.add(user);

    final User presentUser = client.getById(user.getId(), User.class);
    assertEquals(user, presentUser);

    final OperationResult result = client.delete(user);
    assertEquals("0 (success)", result.toString());

    assertThrows(SlcwException.class, () -> client.getById(user.getId(), User.class));
  }

  @Test
  public void shouldReturnSuccessOnUpdateOperation() throws LDAPException {
    final User user = new User("inetOrgPerson", "Name", "Surname", 6785949);

    client.add(user);
    final User presentUser = client.getById(user.getId(), User.class);
    assertEquals(user.getPhoneNumber(), presentUser.getPhoneNumber());

    user.setPhoneNumber(11111111);
    final OperationResult result = client.update(user);
    assertEquals("0 (success)", result.toString());

    final User modifiedUser = client.getById(user.getId(), User.class);
    assertEquals(user, modifiedUser);
  }

  /**
   * Total execution time for count: 0.83s, count result is = 80000.
   */
  @Test
  public void shouldCountLargeNumberOfData() {
    for (int i = 0; i < 80000; i++) {
      final User user = new User("inetOrgPerson", "Name" + i, "Surname", i);
      client.add(user);
    }

    final Filter filter = Filter.builder()
        .searchScope(SearchScope.SUB)
        .applyFilter("sn=Surname")
        .applyFilter("objectClass=inetOrgPerson")
        .build();

    final long startTime = System.currentTimeMillis();
    final long count = client.countBy(filter);
    final long endTime = System.currentTimeMillis();

    assertEquals(80000, count);
  }
}
