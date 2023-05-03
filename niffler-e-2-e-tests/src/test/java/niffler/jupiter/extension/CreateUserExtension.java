package niffler.jupiter.extension;

import com.github.javafaker.Faker;
import niffler.db.dao.NifflerUsersDAOJdbc;
import niffler.db.entity.Authority;
import niffler.db.entity.AuthorityEntity;
import niffler.db.entity.UserEntity;
import niffler.jupiter.annotation.CreateUser;
import niffler.model.CurrencyValues;
import org.junit.jupiter.api.extension.*;

import java.util.Arrays;

public class CreateUserExtension implements ParameterResolver, BeforeEachCallback {
    public static ExtensionContext.Namespace NAMESPACE_CREATE_USER = ExtensionContext.Namespace
        .create(CreateUserExtension.class);
    @Override
    public void beforeEach(ExtensionContext context)  {
        CreateUser annotation = context.getRequiredTestMethod()
            .getAnnotation(CreateUser.class);

        if (annotation != null) {
            NifflerUsersDAOJdbc jdbc = new NifflerUsersDAOJdbc();
            Faker faker = new Faker();
            UserEntity user = new UserEntity();
            user.setUsername(faker.name().username());
            user.setPassword("12345");
            user.setFirstname("Name");
            user.setSurname("Surname");
            user.setCurrency(CurrencyValues.KZT);
            user.setEnabled(true);
            user.setAccountNonExpired(true);
            user.setAccountNonLocked(true);
            user.setCredentialsNonExpired(true);
            user.setAuthorities(Arrays.stream(Authority.values()).map(
                    a -> {
                        AuthorityEntity ae = new AuthorityEntity();
                        ae.setAuthority(a);
                        return ae;
                    }
            ).toList());
            jdbc.createUser(user);
            context.getStore(NAMESPACE_CREATE_USER).put("user", user);
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext,
        ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(UserEntity.class);
    }

    @Override
    public UserEntity resolveParameter(ParameterContext parameterContext,
        ExtensionContext extensionContext) throws ParameterResolutionException {
        return extensionContext.getStore(NAMESPACE_CREATE_USER).get("user", UserEntity.class);
    }
}
