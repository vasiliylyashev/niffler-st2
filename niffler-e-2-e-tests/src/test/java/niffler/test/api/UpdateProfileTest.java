package niffler.test.api;

import io.qameta.allure.Allure;
import niffler.api.UserProfileService;
import niffler.jupiter.annotation.ClasspathUser;
import niffler.jupiter.extension.NegativeProfileUpdateExtension;
import niffler.model.UserJson;
import niffler.test.BaseWebTest;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;

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
        Allure.step("Send update for user");
        int statusCode = userUpdateService.updateUserInfo(user).execute().code();

        Allure.step("Check response status code");
        Assertions.assertEquals(200, statusCode);
        List<UserJson> allUsers = userUpdateService.allUsers(user.getUsername()).execute().body();

        Allure.step("Check that user contains expected values in profile");
        allUsers.stream().filter(userJson -> userJson.getUsername().equals(user.getUsername())).forEach(userJson -> {
            Assertions.assertEquals(userJson.getFirstname(), user.getFirstname());
            Assertions.assertEquals(userJson.getSurname(), user.getSurname());
            Assertions.assertEquals(userJson.getCurrency(), user.getCurrency());
            Assertions.assertEquals(userJson.getPhoto(), user.getPhoto());
        });
    }


    @ValueSource(strings = {
            "testdata/updateProfile/longStrings.json"

    })
    @ParameterizedTest

    public void updateProfileNegativeLongStrings(@ClasspathUser UserJson user) throws IOException {
        Allure.step("Send update for user");
        Response response = userUpdateService.updateUserInfo(user).execute().raw();
        Assertions.assertEquals(500, response.code());
    }

    @ValueSource(strings = {
            "testdata/updateProfile/negative_currency_wrongValue.json",
            "testdata/updateProfile/negative_currency_empty.json"

    })
    @ParameterizedTest
    @ExtendWith(NegativeProfileUpdateExtension.class)
    public void updateProfileNegativeCurrency(@ClasspathUser UserJson user) throws IOException {
        Allure.step("Send negative update for user");
        userUpdateService.updateUserInfo(user).execute();
    }
}
