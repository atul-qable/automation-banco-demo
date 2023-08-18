package bradesco.custodia.bc2.performaction.autoapi;

import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.HashMap;
import java.util.Map;

public class CognitoAccessTokenGenerator {

    public static void main(String[] args) {
//        CognitoAccessTokenGenerator.signInCognitoCodeUsingPassword();
    }

    public String signInCognitoCodeUsingPassword(){
        String region = "ap-south-1";
        String userPoolId = "ap-south-1_7tNuI9JRh";
        String clientId = "5vfojrtrf7g4p49m8igbog352f";
        String email = "kmodiwork@gmail.com";
        String password = "Kush@123";
        String accessToken = null;

        Region awsRegion = Region.of(region);

        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(awsRegion)
                .credentialsProvider(AnonymousCredentialsProvider.create())
                .build();

        Map<String, String> authParams = new HashMap<>();
        authParams.put("USERNAME", email);
        authParams.put("PASSWORD", password);

        InitiateAuthRequest authRequest = InitiateAuthRequest.builder()
                .authFlow(AuthFlowType.USER_PASSWORD_AUTH)
                .clientId(clientId)
                .authParameters(authParams)
                .build();

        try {
            InitiateAuthResponse authResponse = cognitoClient.initiateAuth(authRequest);

            if (authResponse != null && authResponse.authenticationResult() != null) {
                AuthenticationResultType authenticationResult = authResponse.authenticationResult();
                accessToken = authenticationResult.accessToken();
                System.out.println("Access Token: " + accessToken);
            } else {
                System.out.println("Authentication failed.");
            }
        } catch (CognitoIdentityProviderException e) {
            System.err.println("Authentication error: " + e.getMessage());
        }
        return accessToken;
    }
    public static void signInCognitoCodeUsingOtp(){

    }
}
