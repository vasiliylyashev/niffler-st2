package niffler.test.api;

import io.qameta.allure.Allure;
import niffler.api.UserProfileService;
import niffler.jupiter.annotation.ClasspathUser;
import niffler.jupiter.annotation.WrongCurrency;
import niffler.model.UserJson;
import niffler.test.BaseWebTest;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.provider.ValueSource;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Objects;

@WrongCurrency
public class UpdateProfileTest extends BaseWebTest {

    private static final OkHttpClient httpClient = new OkHttpClient.Builder()
            .build();

    private static final Retrofit retrofit = new Retrofit.Builder()
            .client(httpClient)
            .baseUrl("http://127.0.0.1:8089")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final UserProfileService userUpdateService = retrofit.create(UserProfileService.class);

    @ValueSource(strings = {
            "testdata/updateProfile/positive_full.json",
            "testdata/updateProfile/only_name.json",
            "testdata/updateProfile/only_surname.json",
            "testdata/updateProfile/only_photo.json"

    })
    @ParameterizedTest
    public void updateProfilePositive(@ClasspathUser UserJson user) throws IOException {
        Allure.step("Send update for user", () -> {
            retrofit2.Response<UserJson> response = userUpdateService.updateUserInfo(user).execute();
            Allure.step("Check response status code", ()->{
                Assertions.assertEquals(200, response.code());
            });
        });

        UserJson actualUser = Objects.requireNonNull(userUpdateService.allUsers(user.getUsername()).execute().body())
                .stream().filter(userJson -> userJson.getUsername().equals(user.getUsername())).findFirst().orElseThrow();

        Allure.step("Check that user contains expected values in profile", () -> {
            Assertions.assertEquals(actualUser.getFirstname(), user.getFirstname());
            Assertions.assertEquals(actualUser.getSurname(), user.getSurname());
            Assertions.assertEquals(actualUser.getCurrency(), user.getCurrency());
            Assertions.assertEquals(actualUser.getPhoto(), user.getPhoto());
        });
    }


    @ValueSource(strings = {
            "testdata/updateProfile/longStrings.json"
    })
    @ParameterizedTest
    public void updateProfileNegativeLongStrings(@ClasspathUser UserJson user) throws IOException {
        Allure.step("Send update for user", () -> {
            Response response = userUpdateService.updateUserInfo(user).execute().raw();
            Assertions.assertEquals(500, response.code());
        });
    }
    @ValueSource(strings = {
            "testdata/updateProfile/negative_currency_wrongValue.json",
            "testdata/updateProfile/negative_currency_empty.json"
    })
    @ParameterizedTest
    public void updateProfileNegativeWrongCurrencies(@ClasspathUser UserJson user) throws IOException {
        Allure.step("Send update for user", () -> {
            Response response = null;
            try{
                response = userUpdateService.updateUserInfo(user).execute().raw();
            } catch (ArgumentConversionException e){
                Assertions.assertEquals(500, response.code());
            }
        });
    }
}
