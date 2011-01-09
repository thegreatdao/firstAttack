package com.emptyyourmind.entity;

import org.anddev.andengine.entity.layer.ILayer;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;

public class JetsAnimatedSprite extends AnimatedSprite
{
	private SpriteOnPosistionChangedActionsAggregator slAggregator;
	private int health;
	
	public JetsAnimatedSprite(float pX, float pY, TiledTextureRegion pTiledTextureRegion, ILayer layer, int health)
	{
		super(pX, pY, pTiledTextureRegion);
		this.health = health;
		layer.addEntity(this);
	}
	
	@Override
	protected void onPositionChanged()
	{
		super.onPositionChanged();
		if(slAggregator != null)
		{
			slAggregator.onPositionChanged();
		}
	}

	public void damage(int amount)
	{
		health = health - amount;
	}
	
	public void supply(int amount)
	{
		health = health + amount;
	}
	
	public void setSlAggregator(SpriteOnPosistionChangedActionsAggregator slAggregator)
	{
		this.slAggregator = slAggregator;
	}
	
	public void setiShootable(IShootable iShootable)
	{
		iShootable.shoot();
	}

	public int getHealth()
	{
		return health;
	}

	public boolean isDead()
	{
		return health <= 0;
	}
}
