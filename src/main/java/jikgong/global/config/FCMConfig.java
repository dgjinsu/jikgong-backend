//package jikgong.global.config;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import com.google.firebase.messaging.FirebaseMessaging;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.List;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.core.io.ClassPathResource;
//
//@Configuration
//@Profile("!test")
//public class FCMConfig {
//
//    @Value("${fcm.path}")
//    private String path;
//
//    @Bean
//    FirebaseMessaging firebaseMessaging() throws IOException {
//        ClassPathResource resource = new ClassPathResource(path);
//
//        InputStream refreshToken = resource.getInputStream();
//
//        FirebaseApp firebaseApp = null;
//        List<FirebaseApp> firebaseAppList = FirebaseApp.getApps();
//
//        if (firebaseAppList != null && !firebaseAppList.isEmpty()) {
//            for (FirebaseApp app : firebaseAppList) {
//                if (app.getName().equals(FirebaseApp.DEFAULT_APP_NAME)) {
//                    firebaseApp = app;
//                }
//            }
//        } else {
//            FirebaseOptions options = FirebaseOptions.builder()
//                .setCredentials(GoogleCredentials.fromStream(refreshToken))
//                .build();
//
//            firebaseApp = FirebaseApp.initializeApp(options);
//        }
//
//        return FirebaseMessaging.getInstance(firebaseApp);
//    }
//}
