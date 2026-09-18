package com.browl.seacreatures.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.browl.seacreatures.SeaCreaturesGame;
import com.browl.seacreatures.model.Friend;
import com.browl.seacreatures.system.CreatureMovement;
import com.browl.seacreatures.system.GameState;
import com.browl.seacreatures.ui.AddFriendDialog;
import com.browl.seacreatures.ui.BubblePopDialog;
import com.browl.seacreatures.ui.FriendDetailDialog;
import com.browl.seacreatures.ui.ShopDialog;

import java.util.List;

/** The main game screen: the aquarium itself, with the bottom nav bar and coin/notification HUD. */
public class AquariumScreen implements Screen {
    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 480;

    private final SeaCreaturesGame game;
    private final GameState state;

    private final OrthographicCamera camera = new OrthographicCamera();
    private final ExtendViewport viewport = new ExtendViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private final SpriteBatch batch = new SpriteBatch();
    private final CreatureRenderer creatureRenderer = new CreatureRenderer();
    private final CreatureMovement movement = new CreatureMovement();

    private final Stage stage;
    private final Label coinsLabel;
    private final Label notificationLabel;

    private float animTime = 0f;
    private float notificationTimer = 0f;

    public AquariumScreen(SeaCreaturesGame game) {
        this.game = game;
        this.state = game.getGameState();
        this.stage = new Stage(viewport, batch);

        Table root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        Table topBar = new Table();
        coinsLabel = new Label("Coins: " + state.getCoins(), game.getUiFactory().getSkin());
        topBar.add(coinsLabel).expandX().left().pad(10);
        root.add(topBar).expandX().fillX().top().row();

        notificationLabel = new Label("", game.getUiFactory().getSkin());
        notificationLabel.setWrap(true);
        root.add(notificationLabel).width(700).padTop(4).row();

        root.add().expand().row();

        Table bottomBar = new Table();
        bottomBar.add(navButton("Add Friend", this::openAddFriend)).pad(6);
        bottomBar.add(navButton("Shop", this::openShop)).pad(6);
        bottomBar.add(navButton("Bubble Pop", this::openBubblePop)).pad(6);
        bottomBar.add(navButton("Save", this::saveGame)).pad(6);
        root.add(bottomBar).padBottom(10).row();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new AquariumTapListener());
        Gdx.input.setInputProcessor(multiplexer);
    }

    private TextButton navButton(String text, Runnable action) {
        TextButton button = new TextButton(text, game.getUiFactory().getSkin());
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        return button;
    }

    private void openAddFriend() {
        new AddFriendDialog(stage, game.getUiFactory(), game.getSaveManager(), game.getPhotoProvider())
            .show(friend -> {
                state.getFriends().add(friend);
                state.notify(friend.getName() + " has joined the aquarium as a " + friend.getCreatureType().getDisplayName() + "!");
            });
    }

    private void openShop() {
        new ShopDialog(stage, game.getUiFactory()).show(state, () -> {});
    }

    private void openBubblePop() {
        new BubblePopDialog(stage, game.getUiFactory()).show(state, () -> {});
    }

    private void saveGame() {
        game.save();
        state.notify("Game saved.");
    }

    private class AquariumTapListener extends InputAdapter {
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            com.badlogic.gdx.math.Vector3 world = new com.badlogic.gdx.math.Vector3(screenX, screenY, 0);
            camera.unproject(world);
            for (Friend friend : state.getFriends()) {
                float size = CreatureRenderer.sizeFor(friend.getCreatureType());
                float dx = world.x - friend.getX();
                float dy = world.y - friend.getY();
                if (dx * dx + dy * dy <= (size / 2f) * (size / 2f)) {
                    new FriendDetailDialog(stage, game.getUiFactory()).show(friend, state, () -> {});
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        animTime += delta;
        notificationTimer += delta;

        for (Friend friend : state.getFriends()) {
            movement.update(friend, delta, 40, WORLD_WIDTH - 40, 90, WORLD_HEIGHT - 90);
        }

        List<String> events = game.getEventManager().update(state, delta);
        if (!events.isEmpty()) {
            notificationLabel.setText(events.get(events.size() - 1));
            notificationTimer = 0f;
        }
        if (notificationTimer > 6f) {
            notificationLabel.setText("");
        }
        coinsLabel.setText("Coins: " + state.getCoins());

        clearScreen();

        camera.update();
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.05f, 0.30f, 0.55f, 1f));
        shapeRenderer.rect(0, 0, WORLD_WIDTH, WORLD_HEIGHT);
        shapeRenderer.setColor(new Color(0.85f, 0.75f, 0.45f, 1f));
        shapeRenderer.rect(0, 0, WORLD_WIDTH, 50);
        for (Friend friend : state.getFriends()) {
            creatureRenderer.drawBody(shapeRenderer, friend, animTime);
        }
        shapeRenderer.end();

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Friend friend : state.getFriends()) {
            creatureRenderer.drawFace(batch, friend, game.getImageManager(), animTime);
        }
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    private void clearScreen() {
        Gdx.gl.glClearColor(0.02f, 0.15f, 0.25f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void pause() {
        game.save();
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
        batch.dispose();
    }
}
