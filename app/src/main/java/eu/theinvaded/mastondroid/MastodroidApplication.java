package eu.theinvaded.mastondroid;

import android.app.Application;
import android.content.Context;

import eu.theinvaded.mastondroid.data.MastodroidFactory;
import eu.theinvaded.mastondroid.data.MastodroidService;

/**
 * Created by alin on 09.12.2016.
 */

public class MastodroidApplication extends Application {
    private MastodroidService mastodroidService;

    private static MastodroidApplication get(Context context) {
        return (MastodroidApplication) context.getApplicationContext();
    }

    public static MastodroidApplication create(Context context) {
        return MastodroidApplication.get(context);
    }

    public MastodroidService getMastodroidService(String credentials) {

        if (mastodroidService == null){
            mastodroidService = MastodroidFactory.create(credentials);
        }

        return mastodroidService;
    }

    public MastodroidService getMastodroidLoginService() {
        return MastodroidFactory.createLogin();
    }
}
