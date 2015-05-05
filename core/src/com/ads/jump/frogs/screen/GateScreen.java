package com.ads.jump.frogs.screen;

import com.ads.jump.frogs.Answer;
import com.ads.jump.frogs.Assets;
import com.ads.jump.frogs.Settings;
import com.ads.jump.frogs.actors.GateImageBtn;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.repeat;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Created by Administrator on 2014/6/22.
 */
public class GateScreen extends OtherScreen {
    private Image leftBtn;
    private Image rightBtn;
    private MainScreen mainScreen;
    private GameScreen gameScreen;
    private GateImageBtn[] gateImageBtns;
    private Label[] gateNumLabs;
    private Label.LabelStyle yellowStyle;
    private int gatePage = 0;
    private BitmapFont font;

    public GateScreen(MainScreen ms) {
        super(ms.getAppGame());
        mainScreen = ms;
        gameScreen = new GameScreen(this);
        gateImageBtns = new GateImageBtn[Assets.SCREEN_GATE_MAX];
        gateNumLabs = new Label[Assets.SCREEN_GATE_MAX];
        font = getOtherFont();
        font.setScale(Assets.WIDTH / 480 * (float)0.8);
        yellowStyle = new Label.LabelStyle(font, Color.GREEN);
    }

    @Override
    public void show() {
        if (!isShow()) {
            super.show();
            float btnSize = Assets.WIDTH / 5;
            leftBtn = new Image(new TextureRegionDrawable(Assets.levelPreBtn));
            leftBtn.setBounds(0, 0, btnSize, btnSize);
            leftBtn.setOrigin(btnSize/2, btnSize/2);
            float moveX = Assets.WIDTH / 16;
            float duration = (float) 0.5;
            Action complexAction = repeat(2000, sequence(moveTo(moveX, 0, duration), moveTo(0, 0, duration)));
            leftBtn.addAction(complexAction);
            rightBtn = new Image(new TextureRegionDrawable(Assets.levelNextBtn));
            float x = Assets.WIDTH - btnSize;
            rightBtn.setBounds(x, 0, btnSize, btnSize);
            rightBtn.setOrigin(btnSize / 2, btnSize / 2);
            complexAction = repeat(2000, sequence(moveTo(x - moveX, 0, duration), moveTo(x, 0, duration)));
            rightBtn.addAction(complexAction);
            addBtnListens();
            btnVisiableHandle(0);
            buildGateImage(0);
            setShow(true);
        } else {
            Gdx.input.setInputProcessor(getStage());
            setStarNum();
        }
    }

    private void addBtnListens() {
        leftBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Rectangle bound = new Rectangle(0, 0, leftBtn.getWidth(), leftBtn.getHeight());
                if (bound.contains(x, y)) {
                    Assets.playSound(Assets.btnSound);
                    gatePage --;
                    buildGateImage(gatePage);
                }
                super.touchUp(event, x, y, pointer, button);
            }
        });
        rightBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                return true;
            }
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Rectangle bound = new Rectangle(0, 0, rightBtn.getWidth(), rightBtn.getHeight());
                if (bound.contains(x, y)) {
                    Assets.playSound(Assets.btnSound);
                    gatePage ++;
                    buildGateImage(gatePage);
                }
                super.touchUp(event, x, y, pointer, button);
            }
        });
        returnBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Rectangle bound = new Rectangle(0, 0, returnBtn.getWidth(), returnBtn.getHeight());
                if (bound.contains(x, y)) {
                    Assets.playSound(Assets.btnSound);
                    getAppGame().setScreen(getAppGame().getMainScreen());
                }
                super.touchUp(event, x, y, pointer, button);
            }
        });
    }

    private void btnVisiableHandle(int gatePage) {
        if (gatePage == 0) {
            leftBtn.remove();
            if (rightBtn.getStage() == null) {
                addActor(rightBtn);
            }
        } else if (gatePage == Assets.GATE_PAGE_MAX) {
            rightBtn.remove();
            if (leftBtn.getStage() == null) {
                addActor(leftBtn);
            }
        } else {
            if (rightBtn.getStage() == null) {
                addActor(rightBtn);
            }
            if (leftBtn.getStage() == null) {
                addActor(leftBtn);
            }
        }
    }

    @Override
    protected String getStarNumInfo() {
        int starNum = 0;
        for (Integer num : Answer.gateStars) {
            starNum = starNum + num;
        }
        return starNum + "/" + Assets.GATE_MAX * 3;
    }

    public void buildGateImage(final int gp) {
        gatePage = gp;
        btnVisiableHandle(gp);
        removeActor();
        for (int i = 0; i < Assets.SCREEN_GATE_MAX; i++) {
            final int gateNum = gatePage * Assets.SCREEN_GATE_MAX + i;
            if (gateNum > Assets.GATE_MAX - 1) {
                break;
            }
            TextureRegion gateTRegion = null;
            if (Settings.unlockGateNum >= gateNum || gateNum == 0) {
                int num = Answer.gateStars.get(gateNum);
                switch (num) {
                    case 0:
                        gateTRegion = Assets.gate_0star;
                        break;
                    case 1:
                        gateTRegion = Assets.gate_1star;
                        break;
                    case 2:
                        gateTRegion = Assets.gate_2star;
                        break;
                    case 3:
                        gateTRegion = Assets.gate_3star;
                        break;
                }
            } else {
                gateTRegion = Assets.gate_lock;
            }
            addGateActor(i, gateNum, gateTRegion);
        }
    }

    private void addGateActor(int i, final int gateNum, TextureRegion gateTRegion) {
        final GateImageBtn gateImageBtn = new GateImageBtn(new TextureRegionDrawable(gateTRegion), i);
        gateImageBtn.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y,
                                     int pointer, int button) {
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                Rectangle bound = new Rectangle(0, 0, gateImageBtn.getWidth(), gateImageBtn.getHeight());
                if (bound.contains(x, y)) {
                    Assets.playSound(Assets.btnSound);
                    if (gateNum <= Settings.unlockGateNum) {
                        gameScreen.handleNewGate(gateNum);
                        getAppGame().setScreen(gameScreen);
                    }
                }
                super.touchUp(event, x, y, pointer, button);
            }
        });
        gateImageBtns[i] = gateImageBtn;
        addActor(gateImageBtn);
        String temp = (gateNum + 1) + "";
        Label labGateNum = new Label(temp, yellowStyle);
        BitmapFont.TextBounds bounds = font.getBounds(temp);
        labGateNum.setPosition(gateImageBtn.getX(), gateImageBtn.getY() + gateImageBtn.getHeight() - bounds.height);
        gateNumLabs[i] = labGateNum;
        addActor(labGateNum);
    }

    private void removeActor() {
        for (GateImageBtn gateImageBtn : gateImageBtns) {
            if (gateImageBtn != null) {
                gateImageBtn.remove();
            }
        }
        for (Label label : gateNumLabs) {
            if (label != null) {
                label.remove();
            }
        }
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.BACK)) {
            if (!isBackFlag()) {
                mainScreen.setTouchBack(false);
                getAppGame().setScreen(mainScreen);
                return;
            }
        } else {
            setBackFlag(false);
        }
        super.render(delta);
    }

    public void setGatePage(int gatePage) {
        this.gatePage = gatePage;
    }
}
