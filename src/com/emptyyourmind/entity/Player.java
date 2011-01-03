package com.emptyyourmind.entity;

import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

public class Player extends AnimatedSprite
{
	private IPositionChangedListener iPositionChangedListener;
	
	public Player(int pX, int pY, TiledTextureRegion pTiledTextureRegion, IPositionChangedListener iPositionChangedListener)
	{
		super(pX, pY, pTiledTextureRegion);
		this.iPositionChangedListener = iPositionChangedListener;
	}

	
	@Override
	protected void onPositionChanged()
	{
		super.onPositionChanged();
		if(iPositionChangedListener != null)
		{
			iPositionChangedListener.onPositionChanged(mX, mY);
		}
	}

	public interface IPositionChangedListener
	{
		public void onPositionChanged(final float posX, final float posY);
	}

}
