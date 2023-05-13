package niffler.jupiter.extension;

import io.qameta.allure.AllureId;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import niffler.jupiter.annotation.User;
import niffler.jupiter.annotation.User.UserType;
import niffler.model.UserJson;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

public class UsersQueueExtension implements
    BeforeEachCallback,
    AfterTestExecutionCallback,
    ParameterResolver {

  public static Namespace USER_EXTENSION_NAMESPACE = Namespace.create(UsersQueueExtension.class);

  private static final Queue<UserJson> USERS_WITH_FRIENDS_QUEUE = new ConcurrentLinkedQueue<>();
  private static final Queue<UserJson> USERS_INVITATION_SENT_QUEUE = new ConcurrentLinkedQueue<>();
  private static final Queue<UserJson> USERS_INVITATION_RECEIVED_QUEUE = new ConcurrentLinkedQueue<>();

  static {
    USERS_WITH_FRIENDS_QUEUE.addAll(
        List.of(userJson("dima", "12345"), userJson("barsik", "12345"))
    );
    USERS_INVITATION_SENT_QUEUE.addAll(
        List.of(userJson("emma", "12345"), userJson("emily", "12345"))
    );
    USERS_INVITATION_RECEIVED_QUEUE.addAll(
        List.of(userJson("anna", "12345"), userJson("bill", "12345"))
    );
  }

  @Override
  public void beforeEach(ExtensionContext context)  {
    final String testId = getTestId(context);
    Parameter[] testParameters = context.getRequiredTestMethod().getParameters();
    HashMap<Map<UserType, UserJson>, Boolean> usersList = new HashMap<>();
    for (Parameter parameter : testParameters) {
      User desiredUser = parameter.getAnnotation(User.class);
      if (null != desiredUser) {
        UserType userType = desiredUser.userType();
        UserJson user = null;
        while (user == null) {
          switch (userType) {
            case WITH_FRIENDS -> user = USERS_WITH_FRIENDS_QUEUE.poll();
            case INVITATION_SENT -> user = USERS_INVITATION_SENT_QUEUE.poll();
            case INVITATION_RECEIVED -> user = USERS_INVITATION_RECEIVED_QUEUE.poll();
          }
        }
        HashMap<UserType, UserJson> map = new HashMap<>();
        map.put(userType, user);
        usersList.put(map, false);
      }
    }
    context.getStore(USER_EXTENSION_NAMESPACE).put(testId, usersList);
  }

  @SuppressWarnings("unchecked")
  @Override
  public void afterTestExecution(ExtensionContext context)  {
    final String testId = getTestId(context);
    HashMap<Map<UserType, UserJson>, Boolean> usersList = (HashMap<Map<UserType, UserJson>, Boolean>)context.getStore(USER_EXTENSION_NAMESPACE)
            .get(testId);
    for(Map.Entry<Map<UserType, UserJson>, Boolean> usersMap : usersList.entrySet()){
      for (Map.Entry<UserType, UserJson> user : usersMap.getKey().entrySet()) {
        UserType userType = user.getKey();
        switch (userType) {
          case WITH_FRIENDS -> USERS_WITH_FRIENDS_QUEUE.add(user.getValue());
          case INVITATION_SENT -> USERS_INVITATION_SENT_QUEUE.add(user.getValue());
          case INVITATION_RECEIVED -> USERS_INVITATION_RECEIVED_QUEUE.add(user.getValue());
        }
      }
    }
}

  @Override
  public boolean supportsParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().isAnnotationPresent(User.class) &&
        parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
  }

  @SuppressWarnings("unchecked")
  @Override
  public UserJson resolveParameter(ParameterContext parameterContext,
      ExtensionContext extensionContext) throws ParameterResolutionException {
    final String testId = getTestId(extensionContext);
    UserJson requiredUser = null;
    HashMap<Map<UserType, UserJson>, Boolean> usersList = (HashMap<Map<UserType, UserJson>, Boolean>) extensionContext.getStore(USER_EXTENSION_NAMESPACE)
            .get(testId);
    Parameter parameter = extensionContext.getRequiredTestMethod().getParameters()[parameterContext.getIndex()];
    String userType = ((User)parameter.getDeclaredAnnotations()[0]).userType().name();

    for (Map.Entry<Map<UserType, UserJson>, Boolean> entry :
            usersList.entrySet()) {
      if(entry.getValue().equals(false)){
        if(entry.getKey().keySet().stream().findFirst().get().name().equals(userType)){
          requiredUser = entry.getKey().get(UserType.valueOf(userType));
          entry.setValue(true);
          break;
        }
      }
    }
    return requiredUser;
  }

  private String getTestId(ExtensionContext context) {
    return Objects
        .requireNonNull(context.getRequiredTestMethod().getAnnotation(AllureId.class))
        .value();
  }

  private static UserJson userJson(String userName, String password) {
    UserJson user = new UserJson();
    user.setUsername(userName);
    user.setPassword(password);
    return user;
  }
}
