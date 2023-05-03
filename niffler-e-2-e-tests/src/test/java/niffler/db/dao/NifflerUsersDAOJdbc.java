package niffler.db.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import niffler.db.DataSourceProvider;
import niffler.db.ServiceDB;
import niffler.db.entity.AuthorityEntity;
import niffler.db.entity.UserEntity;
import niffler.model.CurrencyValues;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

public class NifflerUsersDAOJdbc implements NifflerUsersDAO {
  PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  private static final DataSource ds = DataSourceProvider.INSTANCE.getDataSource(ServiceDB.NIFFLER_AUTH);
  @Override
  public UserEntity readUser(String username){
    ResultSet result;
    UserEntity selectedUser = new UserEntity();
    try (Connection conn = ds.getConnection();
         PreparedStatement st = conn.prepareStatement("SELECT username, currency, firstname, surname, photo" +
                 "FROM public.users WHERE username=?;")) {
      st.setString(1, username);

      result = st.executeQuery();

      while (result.next()){
        selectedUser.setUsername(result.getString(1));
        selectedUser.setCurrency(CurrencyValues.valueOf(result.getString(2)));
        selectedUser.setFirstname(result.getString(3));
        selectedUser.setSurname(result.getString(4));
        selectedUser.setPhoto(result.getString(5).getBytes());
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return selectedUser;
  }
  @Override
  public int deleteUser(UserEntity user) throws SQLException {
    int executeUpdate;

    try (Connection conn = ds.getConnection()){
      conn.setAutoCommit(false);
      try(PreparedStatement st = conn.prepareStatement("DELETE FROM users WHERE username=?");
              PreparedStatement st2 = conn.prepareStatement("DELETE FROM authorities WHERE user_id=? AND authority=?");){
          st.setString(1, user.getUsername());
          for(AuthorityEntity au : user.getAuthorities()){
            st2.setString(1, user.getUsername());
            st2.setString(2, au.toString());
            st2.addBatch();
          }
          st2.executeBatch();
          executeUpdate = st.executeUpdate();
          if (executeUpdate > 0) {
            System.out.println("An existing user was updated successfully!");
          }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return executeUpdate;
  }
  }

  @Override
  public int updateUser(UserEntity user){
    int executeUpdate;

    try (Connection conn = ds.getConnection();
         PreparedStatement st = conn.prepareStatement("UPDATE users SET"
                 + " password=?, enabled=?, account_non_expired=?, account_non_locked=?, credentials_non_expired=? "
                 + " WHERE username=?")) {
      st.setString(6, user.getUsername());
      st.setString(1, user.getPassword());
      st.setBoolean(2, user.getEnabled());
      st.setBoolean(3, user.getAccountNonExpired());
      st.setBoolean(4, user.getAccountNonLocked());
      st.setBoolean(5, user.getCredentialsNonExpired());

      executeUpdate = st.executeUpdate();
      if (executeUpdate > 0) {
        System.out.println("An existing user was updated successfully!");
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return executeUpdate;
  }
  @Override
  public int createUser(UserEntity user) {
    int executeUpdate = 0;

    try (Connection conn = ds.getConnection()){
      conn.setAutoCommit(false);
      try (
              PreparedStatement st = conn.prepareStatement("INSERT INTO users "
                      + "(username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) "
                      + " VALUES (?, ?, ?, ?, ?, ?)");
              PreparedStatement st2 = conn
                      .prepareStatement("INSERT INTO authorities (user_id, authority) VALUES (?, ?)")) {
        st.setString(1, user.getUsername());
        st.setString(2, pe.encode(user.getPassword()));
        st.setBoolean(3, user.getEnabled());
        st.setBoolean(4, user.getAccountNonExpired());
        st.setBoolean(5, user.getAccountNonLocked());
        st.setBoolean(6, user.getCredentialsNonExpired());
        executeUpdate = st.executeUpdate();
        conn.commit();
        final String userId = getUserId(user.getUsername());
        for(AuthorityEntity au : user.getAuthorities()){
          st2.setString(1, userId);
          st2.setString(2, au.getAuthority().name());
          st2.addBatch();
        }
        st2.executeBatch();
      } catch (SQLException e) {
        conn.rollback();
        conn.setAutoCommit(true);
      }
      conn.commit();
      conn.setAutoCommit(true);
    } catch (SQLException e){
    }
    return executeUpdate;
  }

  @Override
  public String getUserId(String userName) {
    try (Connection conn = ds.getConnection();
        PreparedStatement st = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
      st.setString(1, userName);
      ResultSet resultSet = st.executeQuery();
      if (resultSet.next()) {
        return resultSet.getString(1);
      } else {
        throw new IllegalArgumentException("Can`t find user by given username: " + userName);
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
