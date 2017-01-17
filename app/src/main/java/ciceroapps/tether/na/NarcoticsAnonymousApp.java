package ciceroapps.tether.na;

import com.firebase.client.Firebase;

public class NarcoticsAnonymousApp extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
