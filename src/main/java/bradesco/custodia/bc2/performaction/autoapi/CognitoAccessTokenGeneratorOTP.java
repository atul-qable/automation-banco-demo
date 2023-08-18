package bradesco.custodia.bc2.performaction.autoapi;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.util.Map;

public class CognitoAccessTokenGeneratorOTP {
    public String signInCognitoCodeUsingOTP(String mobileNumber) {
        // Replace with your actual values

        String region = "ap-south-1";
        String clientId = "pfndeosf0t8p4rj72k71362tc";
        String email = "kmodiwork@gmail.com";
        String password = "Kush@123";
        String accessToken = null;
        String phoneNumber = mobileNumber;// User's phone number
        String otp = "123456";

        CognitoIdentityProviderClient cognitoClient = CognitoIdentityProviderClient.builder()
                .region(Region.of(region))
                .build();

        // Initiate authentication flow
        InitiateAuthRequest initiateAuthRequest = InitiateAuthRequest.builder()
                .authFlow(AuthFlowType.CUSTOM_AUTH)
                .authParameters(
                        Map.of(
                                "USERNAME", phoneNumber,
                                "PASSWORD", otp
                        )
                )
                .clientId(clientId)
                .build();

        InitiateAuthResponse initiateAuthResponse = cognitoClient.initiateAuth(initiateAuthRequest);

        String session = initiateAuthResponse.session(); // Obtain the session token from the response

        RespondToAuthChallengeRequest challengeRequest = RespondToAuthChallengeRequest.builder()
                .clientId(clientId)
                .challengeName(ChallengeNameType.CUSTOM_CHALLENGE.toString())
                .challengeResponses(
                        Map.of(
                                "USERNAME", phoneNumber,
                                "ANSWER", otp
                        )
                )
                .session(session)  // Use the session token obtained from the authentication response
                .build();

        RespondToAuthChallengeResponse challengeResponse = cognitoClient.respondToAuthChallenge(challengeRequest);

        accessToken = challengeResponse.authenticationResult().accessToken();
        String idToken = challengeResponse.authenticationResult().idToken();

        System.out.println("Access Token: " + accessToken);
        System.out.println("ID Token: " + idToken);
        return accessToken;
    }
}
