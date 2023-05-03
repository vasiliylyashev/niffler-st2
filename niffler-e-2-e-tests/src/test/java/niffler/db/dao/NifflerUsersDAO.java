package niffler.db.dao;

import niffler.db.entity.UserEntity;
import java.sql.SQLException;

public interface NifflerUsersDAO {

  UserEntity readUser(String username);

  int deleteUser(UserEntity user) throws SQLException;

  int updateUser(UserEntity user);

  int createUser(UserEntity user);

  String getUserId(String userName);

}
