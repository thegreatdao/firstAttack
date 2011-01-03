package com.emptyyourmind;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.camera.BoundCamera;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLayer;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXProperties;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTile;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTileProperty;
import org.anddev.andengine.entity.layer.tiled.tmx.TMXTiledMap;
import org.anddev.andengine.entity.layer.tiled.tmx.util.exception.TMXLoadException;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.Scene.IOnSceneTouchListener;
import org.anddev.andengine.entity.shape.modifier.ScaleModifier;
import org.anddev.andengine.entity.shape.modifier.SequenceShapeModifier;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.texture.Texture;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;
import org.anddev.andengine.util.Debug;

import android.view.MotionEvent;

import com.emptyyourmind.entity.Player;


public class Main extends BaseGameActivity implements IOnSceneTouchListener
{
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	private int mapWidth;
	private int mapHeight;
	private boolean headingPositiveX;
	private boolean headingPositiveY;
	private boolean reachHorizontalBoundary;
	private boolean reachVerticalBoundary;

	private BoundCamera mBoundChaseCamera;

	private Texture mTexture;
	private TiledTextureRegion mPlayerTextureRegion;
	private TMXTiledMap mTMXTiledMap;
	protected int mCactusCount;
	private Player player;

	private Texture mOnScreenControlTexture;
	private TextureRegion mOnScreenControlBaseTextureRegion;
	private TextureRegion mOnScreenControlKnobTextureRegion;

	@Override
	public Engine onLoadEngine()
	{
		mBoundChaseCamera = new BoundCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		return new Engine(new EngineOptions(true, ScreenOrientation.LANDSCAPE,
				new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT),
				mBoundChaseCamera));
	}

	@Override
	public void onLoadResources()
	{
		TextureRegionFactory.setAssetBasePath("gfx/");
		mTexture = new Texture(128, 128, TextureOptions.DEFAULT);
		mPlayerTextureRegion = TextureRegionFactory.createTiledFromAsset(
				mTexture, this, "player.png", 0, 0, 3, 1); // 72x128

		mOnScreenControlTexture = new Texture(256, 128,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mOnScreenControlBaseTextureRegion = TextureRegionFactory
				.createFromAsset(mOnScreenControlTexture, this,
						"onscreen_control_base.png", 0, 0);
		mOnScreenControlKnobTextureRegion = TextureRegionFactory
				.createFromAsset(mOnScreenControlTexture, this,
						"onscreen_control_knob.png", 128, 0);

		mEngine.getTextureManager().loadTextures(this.mTexture,
				this.mOnScreenControlTexture);
	}

	@Override
	public Scene onLoadScene()
	{
		mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene(2);

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
		player = new Player(centerX, centerY, mPlayerTextureRegion, new com.emptyyourmind.entity.Player.IPositionChangedListener()
		{
			
			@Override
			public void onPositionChanged(float posX, float posY)
			{
				if(headingPositiveX)
				{
					if(posX + mPlayerTextureRegion.getTileWidth() >= mapWidth)
					{
						reachHorizontalBoundary = true;
					}
					else
					{
						reachHorizontalBoundary = false;
					}
				}
				else
				{
					if(posX <= 0)
					{
						reachHorizontalBoundary = true;
					}
					else
					{
						reachHorizontalBoundary = false;
					}
				}
				if(headingPositiveY)
				{
					if(posY + mPlayerTextureRegion.getTileHeight() >= mapHeight)
					{
						reachVerticalBoundary = true;
					}
					else
					{
						reachVerticalBoundary = false;
					}
				}
				else
				{
					if(posY <= 0)
					{
						reachVerticalBoundary = true;
					}
					else
					{
						reachVerticalBoundary = false;
					}
				}
			}
		});
		player.animate(100);
		scene.getTopLayer().addEntity(player);
		scene.setOnSceneTouchListener(this);
		mBoundChaseCamera.setChaseShape(player);

		final AnalogOnScreenControl analogOnScreenControl = new AnalogOnScreenControl(
				0, CAMERA_HEIGHT - mOnScreenControlBaseTextureRegion.getHeight(), mBoundChaseCamera, mOnScreenControlBaseTextureRegion, mOnScreenControlKnobTextureRegion, 0.1f, 200,
				new IAnalogOnScreenControlListener()
				{
					@Override
					public void onControlChange(final BaseOnScreenControl pBaseOnScreenControl, final float pValueX, final float pValueY)
					{
						if(pValueX > 0)
						{
							headingPositiveX = true;
						}
						else
						{
							headingPositiveX = false;
						}
						if(pValueY > 0)
						{
							headingPositiveY = true;
						}
						else
						{
							headingPositiveY = false;
						}
						float xValue = pValueX;
						float yValue = pValueY;
						if(reachHorizontalBoundary)
						{
							xValue = 0;
						}
						if(reachVerticalBoundary)
						{
							yValue = 0;
						}
						player.setVelocity(xValue * 60, yValue * 60);
					}

					@Override
					public void onControlClick(final AnalogOnScreenControl pAnalogOnScreenControl)
					{
						player.addShapeModifier(new SequenceShapeModifier(new ScaleModifier(0.25f, 1, 1.5f), new ScaleModifier(0.25f, 1.5f, 1)));
					}
				});
		analogOnScreenControl.getControlBase().setBlendFunction(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		analogOnScreenControl.getControlBase().setAlpha(0.5f);
		analogOnScreenControl.getControlBase().setScaleCenter(0, 128);
		analogOnScreenControl.getControlBase().setScale(0.75f);
		analogOnScreenControl.getControlKnob().setScale(0.75f);
		analogOnScreenControl.refreshControlKnobPosition();

		scene.setChildScene(analogOnScreenControl);

		return scene;
	}

	@Override
	public void onLoadComplete()
	{

	}

	@Override
	public boolean onSceneTouchEvent(Scene pScene, TouchEvent pTouchEvent)
	{
		if (pTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
		{
			pTouchEvent.getMotionEvent().getX();
			pTouchEvent.getMotionEvent().getY();
		}
		return true;
	}

}
