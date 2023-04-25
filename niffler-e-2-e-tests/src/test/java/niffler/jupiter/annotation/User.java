package niffler.jupiter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface User {

  UserType userType();

  enum UserType {
    WITH_FRIENDS ("WITH_FRIENDS"), // dima, barsik
    INVITATION_SENT ("INVITATION_SENT"), // emma, emily
    INVITATION_RECEIVED ("INVITATION_RECEIVED"); // anna, bill

    UserType(String withFriends) {

    }
  }
}
