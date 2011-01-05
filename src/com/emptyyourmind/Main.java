package com.emptyyourmind;

import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.audio.music.Music;
import org.anddev.andengine.audio.music.MusicFactory;
import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.BoundCamera;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.layer.ILayer;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLayer;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXObject;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXObjectGroup;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXProperties;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTile;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTileProperty;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.anddev.andengine.entity.layer.tiled.tmx.util.exception.TMXLoadException;
import org.anddev.andengine.entity.primitive.Line;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.extension.physics.box2d.FixedStepPhysicsWorld;
import org.anddev.andengine.extension.physics.box2d.PhysicsConnector;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.emptyyourmind.entity.CustomizedAnimatedSprite;
import com.emptyyourmind.entity.CustomizedSprite;
import com.emptyyourmind.entity.IPositionChangedListener;


public class Main extends BaseGameActivity implements IOnSceneTouchListener
{
	private static final int CAMERA_WIDTH = 320;
	private static final int CAMERA_HEIGHT = 480;
	private int mapWidth;
	private int mapHeight;
	@SuppressWarnings("unused")
	private Sound explosionSound;
	@SuppressWarnings("unused")
	private Music backgourndMusic;

	private BoundCamera mBoundChaseCamera;

	private PhysicsWorld physicsWorld;

	private Texture playerTexture;
	private Texture enemyBossTexture;
	private TiledTextureRegion mPlayerTextureRegion;
	private TiledTextureRegion enemyBossTextureRegion;
	private TMXTiledMap mTMXTiledMap;
	protected int mCactusCount;
	private CustomizedAnimatedSprite player;
	private Body playerBody;
	
	private Texture mOnScreenControlTexture;
	private Texture bulletTexture;
	private Texture mineTexture;
	private Texture greenBallTexture;
	private TiledTextureRegion mineTextureRegion;
	private TiledTextureRegion greenBallTextureRegion;
	private TextureRegion mOnScreenControlBaseTextureRegion;
	private TextureRegion mOnScreenControlKnobTextureRegion;
	private TextureRegion mBulletTextureRegion;
	private Scene scene;
	private long currentTimeInmillis;
	private static final int SHOT_TIME_INTERVAL = 200;
	private int playerSpawnX;
	private int playerSpawnY;
	@Override
	public Engine onLoadEngine()
	{
		mBoundChaseCamera = new BoundCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.PORTRAIT,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),
				mBoundChaseCamera).setNeedsSound(true).setNeedsMusic(true));
	}

	@Override
	public void onLoadResources()
	{
		TextureRegionFactory.setAssetBasePath("gfx/");
		enemyBossTexture = new Texture(256, 64, TextureOptions.DEFAULT);
		playerTexture = new Texture(64, 32, TextureOptions.DEFAULT);
		enemyBossTextureRegion = TextureRegionFactory.createTiledFromAsset(enemyBossTexture, this, "enemy.png", 0, 0, 2, 1);
		mPlayerTextureRegion = TextureRegionFactory.createTiledFromAsset(
				playerTexture, this, "player.png", 0, 0, 3, 1); // 72x128
		bulletTexture = new Texture(16, 32, TextureOptions.BILINEAR);
		mBulletTextureRegion = TextureRegionFactory.createFromAsset(bulletTexture, this, "bullet2.png", 0, 0);

		mOnScreenControlTexture = new Texture(256, 128,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mOnScreenControlBaseTextureRegion = TextureRegionFactory
				.createFromAsset(mOnScreenControlTexture, this,
						"onscreen_control_base.png", 0, 0);
		mOnScreenControlKnobTextureRegion = TextureRegionFactory
				.createFromAsset(mOnScreenControlTexture, this,
						"onscreen_control_knob.png", 128, 0);
		
		mineTexture = new Texture(64, 32, TextureOptions.DEFAULT);
		greenBallTexture = new Texture(64, 16, TextureOptions.DEFAULT);
		mineTextureRegion = TextureRegionFactory.createTiledFromAsset(mineTexture, this, "mine.png", 0, 0, 3, 1);
		greenBallTextureRegion = TextureRegionFactory.createTiledFromAsset(greenBallTexture, this, "greenBall.png", 0, 0, 4, 1);
		mEngine.getTextureManager().loadTextures(playerTexture, bulletTexture, mOnScreenControlTexture, mineTexture, greenBallTexture, enemyBossTexture);
		
		SoundFactory.setAssetBasePath("mfx/");
		MusicFactory.setAssetBasePath("mfx/");
		try {
			this.explosionSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "explosion.ogg");
			this.backgourndMusic= MusicFactory.createMusicFromAsset(this.mEngine.getMusicManager(), this, "bg.ogg");
		/*	backgourndMusic.setLooping(true);
			backgourndMusic.play();*/
		} catch (final IOException e) {
			Debug.e("Error", e);
		}
	}

	@Override
	public Scene onLoadScene()
	{
		scene = new Scene(4);
		physicsWorld = new FixedStepPhysicsWorld(30, new Vector2(0, 0), false, 8, 1);
		try
		{
			final TMXLoader tmxLoader = new TMXLoader(this,
					mEngine.getTextureManager(),
					TextureOptions.BILINEAR_PREMULTIPLYALPHA,
					new ITMXTilePropertiesListener()
					{
						@Override
						public void onTMXTileWithPropertiesCreated(
								final TMXTiledMap pTMXTiledMap,
								final TMXLayer pTMXLayer,
								final TMXTile pTMXTile,
								final TMXProperties<TMXTileProperty> pTMXTileProperties)
						{
							/*
							 * We are going to count the tiles that have the
							 * property "cactus=true" set.
							 */
							if (pTMXTileProperties.containsTMXProperty(
									"cactus", "true"))
							{
							}
						}
					});
			mTMXTiledMap = tmxLoader.loadFromAsset(this, "tmx/firstAttack.tmx");

		} catch (final TMXLoadException tmxle)
		{
			Debug.e(tmxle);
		}

		final TMXLayer tmxLayer = mTMXTiledMap.getTMXLayers().get(0);
		TMXTile tmxTile = tmxLayer.getTMXTile(5, 5);
		TMXTile tmxTile2 = tmxLayer.getTMXTile(5, 6);
		addObstacles(tmxTile, tmxTile2, scene.getTopLayer(), physicsWorld);
		tmxTile = tmxLayer.getTMXTile(6, 10);
		tmxTile2 = tmxLayer.getTMXTile(10, 10);
		addObstacles(tmxTile, tmxTile2, scene.getTopLayer(), physicsWorld);
		tmxTile = tmxLayer.getTMXTile(8, 8);
		addObstacles(tmxTile, tmxTile, scene.getTopLayer(), physicsWorld);
		
		final TMXLayer tmxLayer2 = mTMXTiledMap.getTMXLayers().get(1);
		final TMXObjectGroup tmxObjectGroup = mTMXTiledMap.getTMXObjectGroups().get(0);
		ArrayList<TMXObject> tmxObjects = tmxObjectGroup.getTMXObjects();
		for(TMXObject tmxObject : tmxObjects)
		{
			if(tmxObject.getName().equals("playerSpawnPoint"))
			{
				playerSpawnX = tmxObject.getX();
				playerSpawnY = tmxObject.getY();
			}
		}
		scene.getBottomLayer().addEntity(tmxLayer);
		scene.getBottomLayer().addEntity(tmxLayer2);
		/* Make the camera not exceed the bounds of the TMXLayer. */
		mBoundChaseCamera.setBounds(0, tmxLayer.getWidth(), 0, tmxLayer.getHeight());		
		mBoundChaseCamera.setBoundsEnabled(true);
		mapWidth = (int)tmxLayer.getWidth();
		mapHeight = (int)tmxLayer.getHeight();

		/*
		 * Calculate the coordinates for the face, so its centered on the
		 * camera.
		 */
		AnimatedSprite enemyBoss = new AnimatedSprite(200, 200, enemyBossTextureRegion);
		enemyBoss.animate(200);
		scene.getTopLayer().addEntity(enemyBoss);
		/* Create the sprite and add it to the scene. */
		player = new CustomizedAnimatedSprite(playerSpawnX, playerSpawnY, mPlayerTextureRegion, new IPositionChangedListener()
		{
			@Override
			public void onPositionChanged(float posX, float posY)
			{
			}
		});
		player.animate(200);
		scene.getTopLayer().addEntity(player);
		player.setUpdatePhysics(false);
		AnimatedSprite mine = new AnimatedSprite(100, 100, mineTextureRegion);
		mine.animate(200);
		scene.getTopLayer().addEntity(mine);
		
		AnimatedSprite greenBall = new AnimatedSprite(200,200, greenBallTextureRegion);
		greenBall.animate(200);
		scene.getTopLayer().addEntity(greenBall);
		final FixtureDef carFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);
		playerBody = PhysicsFactory.createBoxBody(physicsWorld, player, BodyType.DynamicBody, carFixtureDef);
		
		physicsWorld.registerPhysicsConnector(new PhysicsConnector(player, playerBody, true, false, true, false));
		
		setBorder(scene);
		scene.setOnSceneTouchListener(this);
		mBoundChaseCamera.setChaseShape(player);

		final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(
				0, CAMERA_HEIGHT - mOnScreenControlBaseTextureRegion.getHeight(), mBoundChaseCamera, mOnScreenControlBaseTextureRegion, mOnScreenControlKnobTextureRegion, 0.1f, 200,
				new IAnalogOnScreenControlListener()
				{
					private Vector2 mVelocityTemp = new Vector2();
					@Override
					public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY)
					{
						this.mVelocityTemp.set(pValueX * 2, pValueY * 3);
						
						final Body carBody = Main.this.playerBody;
						carBody.setLinearVelocity(this.mVelocityTemp);
						
					}
					@Override
					public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl)
					{
					}
				});
		analogOnScreenControl.getControlBase().setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		analogOnScreenControl.getControlBase().setAlpha(0.5f);
		analogOnScreenControl.getControlBase().setScaleCenter(0, 128);
		analogOnScreenControl.getControlBase().setScale(0.75f);
		analogOnScreenControl.getControlKnob().setScale(0.75f);
		analogOnScreenControl.refreshControlKnobPosition();

		scene.setChildScene(analogOnScreenControl);
//		drawSystem(scene);
		scene.registerUpdateHandler(physicsWorld);

		return scene;
	}

	@Override
	public void onLoadComplete()
	{

	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, TouchEvent pTouchEvent)
	{
		if (pTouchEvent.getAction() == TouchEvent.ACTION_DOWN)
		{
			long now = System.currentTimeMillis();
			if(currentTimeInmillis == 0)
			{
				currentTimeInmillis = now;
			}
			if(now - currentTimeInmillis >= SHOT_TIME_INTERVAL)
			{
				shotBaisicBullets(player, pScene.getTopLayer(), 100, 100);
//				explosionSound.play();
				currentTimeInmillis = now;
			}
		}
		return true;
	}

	@SuppressWarnings("unused")
	private void drawSystem(Scene scene)
	{
		int count = mapHeight / 28;
		
		for(int i = 0; i < count; i++) {
			final float x1 = 0;
			final float x2 = mapWidth;
			final float y1 = i * 28;
			final float y2 = i * 28;
			final float lineWidth = 1;

			final Line line = new Line(x1, y1-1, x2, y2-1, lineWidth);
			final Line line2 = new Line(x1, y1, x2, y2, lineWidth);
			final Line line3 = new Line(x1, y1+1, x2, y2+1, lineWidth);

			line.setColor(0.2f, 0.5f, 0.9f, 0.5f);
			line2.setColor(1, 1, 1, 0.5f);
			line3.setColor(0.2f, 0.5f, 0.9f, 0.5f);

			scene.getLayer(2).addEntity(line);
			scene.getLayer(2).addEntity(line2);
			scene.getLayer(2).addEntity(line3);
		}
		
		count = mapWidth / 24;
		for(int i = 0; i < count; i++) {
			final float x1 = i * 24;
			final float x2 = i * 24;
			final float y1 = 0;
			final float y2 = mapHeight;
			final float lineWidth = 1;

			final Line line = new Line(x1-1, y1, x2-1, y2, lineWidth);
			final Line line2 = new Line(x1, y1, x2, y2, lineWidth);
			final Line line3 = new Line(x1+1, y1, x2+1, y2, lineWidth);

			line.setColor(0.2f, 0.5f, 0.9f, 0.5f);
			line2.setColor(1, 1, 1, 0.5f);
			line3.setColor(0.2f, 0.5f, 0.9f, 0.5f);

			scene.getLayer(2).addEntity(line);
			scene.getLayer(2).addEntity(line2);
			scene.getLayer(2).addEntity(line3);
		}
	}
	
	private void setBorder(final Scene pScene)
	{
		final Shape bottom = new Rectangle(-1, mapHeight + 1, mapWidth, 1);
		final Shape top = new Rectangle(-1, -1, mapWidth, 1);
		final Shape left = new Rectangle(-1, -1, 1, mapHeight);
		final Shape right = new Rectangle(mapWidth+1, -1, 1, mapHeight);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(physicsWorld, bottom, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(physicsWorld, top, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(physicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(physicsWorld, right, BodyType.StaticBody, wallFixtureDef);

		final ILayer bottomLayer = pScene.getLayer(2);
		bottomLayer.addEntity(bottom);
		bottomLayer.addEntity(top);
		bottomLayer.addEntity(left);
		bottomLayer.addEntity(right);
	}
	
	private CustomizedSprite createBullet(float x, float y, final ILayer layer)
	{
		final CustomizedSprite bullet = new CustomizedSprite(x, y, mBulletTextureRegion);
		IPositionChangedListener iPositionChangedListener = new IPositionChangedListener()
		{
			
			@Override
			public void onPositionChanged(float posX, float posY)
			{
				if(posX <=0 || posX >= mapWidth || posY <=0 || posY >= mapHeight)
				{
					runOnUpdateThread(new Runnable()
					{
						
						@Override
						public void run()
						{
							layer.removeEntity(bullet);
						}
					});
				}
			}
		};
		bullet.setiPositionChangedListener(iPositionChangedListener);
		return bullet;
	}
	
	private void shotBaisicBullets(CustomizedAnimatedSprite ship, ILayer layer, float xVelocity, float yVelocity)
	{
		float width = ship.getWidth();
//		float height = ship.getHeight();
		float x = ship.getX();
		float y = ship.getY();
		CustomizedSprite upBullet = createBullet(x + width / 2 - 2, y, layer);
		upBullet.setVelocity(0, -yVelocity);
		layer.addEntity(upBullet);
/*		CustomizedSprite downBullet = createBullet(x + width / 2 -2, y - 2 + height, layer);
		downBullet.setVelocity(0, yVelocity);
		layer.addEntity(downBullet);
		CustomizedSprite rightBullet = createBullet(x + width, y + height / 2 - 2, layer);
		rightBullet.setVelocity(xVelocity, 0);
		layer.addEntity(rightBullet);
		CustomizedSprite leftBullet = createBullet(x - 2, y + height / 2 - 2, layer);
		leftBullet.setVelocity(-xVelocity, 0);
		layer.addEntity(leftBullet);*/
	}
	
	private void addObstacles(TMXTile startTile, TMXTile endTile, ILayer layer, PhysicsWorld physicsWorld)
	{
		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		int borderThickness = 1;
		int tileWidth = startTile.getTileWidth();
		int tileHeight = startTile.getTileHeight();
		int borderWidth = tileWidth;
		int borderHeight = tileHeight;
		if(startTile != endTile)
		{
			borderWidth = tileWidth * (1 + endTile.getTileColumn() - startTile.getTileColumn());
			borderHeight = tileHeight * (1 + endTile.getTileRow() - startTile.getTileRow());
		}
		int originX = startTile.getTileX();
		int originY = startTile.getTileY();
		Shape top = new Rectangle(originX, originY, borderWidth, borderThickness);
		top.setColor(0, 0, 0, 0);
		Shape bottom = new Rectangle(originX, originY + borderHeight, borderWidth, borderThickness);
		bottom.setColor(0, 0, 0, 0);
		Shape left = new Rectangle(originX, originY, borderThickness, borderHeight);
		left.setColor(0, 0, 0, 0);
		Shape right = new Rectangle(originX + borderWidth, originY, borderThickness, borderHeight);
		right.setColor(0, 0, 0, 0);
		layer.addEntity(top);
		layer.addEntity(bottom);
		layer.addEntity(left);
		layer.addEntity(right);
		PhysicsFactory.createBoxBody(physicsWorld, top, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(physicsWorld, bottom, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(physicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(physicsWorld, right, BodyType.StaticBody, wallFixtureDef);
	}
	
}
