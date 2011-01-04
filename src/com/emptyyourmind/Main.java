package com.emptyyourmind;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

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
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	private int mapWidth;
	private int mapHeight;
	private Sound explosionSound;

	private BoundCamera mBoundChaseCamera;

	private PhysicsWorld physicsWorld;

	private Texture playerTexture;
	private TiledTextureRegion mPlayerTextureRegion;
	private TMXTiledMap mTMXTiledMap;
	protected int mCactusCount;
	private CustomizedAnimatedSprite player;
	private Body playerBody;
	
	private Texture mOnScreenControlTexture;
	private TextureRegion mOnScreenControlBaseTextureRegion;
	private TextureRegion mOnScreenControlKnobTextureRegion;
	private TextureRegion mBulletTextureRegion;
	private Texture bulletTexture;
	private Scene scene;
	private long currentTimeInmillis;
	private static final int SHOT_TIME_INTERVAL = 1500;
	@Override
	public Engine onLoadEngine()
	{
		mBoundChaseCamera = new BoundCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),
				mBoundChaseCamera).setNeedsSound(true));
	}

	@Override
	public void onLoadResources()
	{
		TextureRegionFactory.setAssetBasePath("gfx/");
		playerTexture = new Texture(128, 128, TextureOptions.DEFAULT);
		mPlayerTextureRegion = TextureRegionFactory.createTiledFromAsset(
				playerTexture, this, "player.png", 0, 0, 3, 1); // 72x128
		bulletTexture = new Texture(8, 8, TextureOptions.BILINEAR);
		mBulletTextureRegion = TextureRegionFactory.createFromAsset(bulletTexture, this, "bullet.png", 0, 0);

		mOnScreenControlTexture = new Texture(256, 128,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mOnScreenControlBaseTextureRegion = TextureRegionFactory
				.createFromAsset(mOnScreenControlTexture, this,
						"onscreen_control_base.png", 0, 0);
		mOnScreenControlKnobTextureRegion = TextureRegionFactory
				.createFromAsset(mOnScreenControlTexture, this,
						"onscreen_control_knob.png", 128, 0);

		mEngine.getTextureManager().loadTextures(playerTexture, bulletTexture, mOnScreenControlTexture);
		
		SoundFactory.setAssetBasePath("mfx/");
		try {
			this.explosionSound = SoundFactory.createSoundFromAsset(this.mEngine.getSoundManager(), this, "explosion.ogg");
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
								Main.this.mCactusCount++;
							}
						}
					});
			mTMXTiledMap = tmxLoader.loadFromAsset(this, "tmx/firstAttack.tmx");

		} catch (final TMXLoadException tmxle)
		{
			Debug.e(tmxle);
		}

		final TMXLayer tmxLayer = mTMXTiledMap.getTMXLayers().get(0);
		final TMXLayer tmxLayer2 = mTMXTiledMap.getTMXLayers().get(1);
		/*
		 * final TMXLayer tmxLayer3 = mTMXTiledMap.getTMXLayers().get(2); final
		 * TMXLayer tmxLayer4 = mTMXTiledMap.getTMXLayers().get(3);
		 * TMXObjectGroup tmxObjectGroup =
		 * mTMXTiledMap.getTMXObjectGroups().get(0); TMXObject tmxObject =
		 * tmxObjectGroup.getTMXObjects().get(0);
		 */
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

		final int centerX = (CAMERA_WIDTH - mPlayerTextureRegion.getTileWidth()) / 2;
		final int centerY = (CAMERA_HEIGHT - mPlayerTextureRegion.getTileHeight()) / 2;

		/* Create the sprite and add it to the scene. */
		player = new CustomizedAnimatedSprite(centerX, centerY, mPlayerTextureRegion, new IPositionChangedListener()
		{
			@Override
			public void onPositionChanged(float posX, float posY)
			{
			}
		});
		player.animate(100);
		scene.getTopLayer().addEntity(player);
		player.setUpdatePhysics(false);
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
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pTouchEvent)
	{
		if (pTouchEvent.getAction() == TouchEvent.ACTION_DOWN)
		{
			long now = System.currentTimeMillis();
			if(currentTimeInmillis ==0)
			{
				currentTimeInmillis = now;
			}
			if(now - currentTimeInmillis >= SHOT_TIME_INTERVAL)
			{
				
				shotBullets(player, pScene);
				explosionSound.play();
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
	
	private void setBorder(final Scene pScene) {
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
	
	private CustomizedSprite createBullet(float x, float y)
	{
		final CustomizedSprite bullet = new CustomizedSprite(x, y, mBulletTextureRegion);
		IPositionChangedListener iPositionChangedListener = new IPositionChangedListener()
		{
			
			@Override
			public void onPositionChanged(float posX, float posY)
			{
				if(posX <=0 || posX >= mapWidth)
				{
					bullet.setVelocity(-bullet.getVelocityX(), bullet.getVelocityY());
				}
				if(posY <=0 || posY >= mapHeight)
				{
					bullet.setVelocity(bullet.getVelocityX(), -bullet.getVelocityY());
				}
			}
		};
		bullet.setiPositionChangedListener(iPositionChangedListener);
		return bullet;
	}
	
	private void shotBullets(CustomizedAnimatedSprite ship, Scene scene)
	{
		float width = ship.getWidth();
		float height = ship.getHeight();
		float x = ship.getX();
		float y = ship.getY();
		CustomizedSprite upBullet = createBullet(x + width / 2, y);
		upBullet.setVelocity(0, -10);
		scene.getTopLayer().addEntity(upBullet);
		CustomizedSprite downBullet = createBullet(x + width / 2, y + height);
		downBullet.setVelocity(0, 10);
		scene.getTopLayer().addEntity(downBullet);
		CustomizedSprite rightBullet = createBullet(x + width, y + height / 2);
		rightBullet.setVelocity(10, 0);
		scene.getTopLayer().addEntity(rightBullet);
		CustomizedSprite leftBullet = createBullet(x, y + height / 2);
		leftBullet.setVelocity(-10, 0);
		scene.getTopLayer().addEntity(leftBullet);
		
		
	}
	
}
