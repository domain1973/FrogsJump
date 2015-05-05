package com.ads.jump.frogs;

import com.ads.jump.frogs.screen.LoadingScreen;
import com.ads.jump.frogs.screen.MainScreen;
import com.badlogic.gdx.Game;

public class AppGame extends Game {
    private LoadingScreen loadingScreen;
    private PEvent pEvent;

    public AppGame() {
    }

    public AppGame(PEvent pe) {
        pEvent = pe;
    }

    @Override
    public void create() {
        loadingScreen = new LoadingScreen(this);
        setScreen(loadingScreen);
    }

    public MainScreen getMainScreen() {
        return loadingScreen.getMainScreen();
    }

    public PEvent getPEvent() {
        return pEvent;
    }
}
