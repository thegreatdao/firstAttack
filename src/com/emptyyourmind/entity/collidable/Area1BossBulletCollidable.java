package com.emptyyourmind.entity.collidable;

import org.anddev.andengine.entity.layer.ILayer;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import com.emptyyourmind.entity.ICollidable;
import com.emptyyourmind.entity.JetsAnimatedSprite;
import com.emptyyourmind.entity.JetsSprite;

public class Area1BossBulletCollidable implements ICollidable
{
	private JetsAnimatedSprite player;
	private JetsSprite bullet;
	private BaseGameActivity baseGameActivity;
	private ILayer layer;
	private int damage;
	
	public Area1BossBulletCollidable(JetsSprite bullet, JetsAnimatedSprite player, BaseGameActivity baseGameActivity, ILayer layer, int damage)
	{
		this.player = player;
		this.bullet = bullet;
		this.baseGameActivity = baseGameActivity;
		this.layer = layer;
		this.damage = damage;
	}
	
	@Override
	public void collide()
	{
		if (bullet.getY() >= player.getY())
		{
			final JetsAnimatedSprite player = Area1BossBulletCollidable.this.player;
			if(!player.isDead())
			{
				baseGameActivity.runOnUpdateThread(new Runnable()
				{
					@Override
					public void run()
					{
						player.damage(damage);
						Area1BossBulletCollidable.this.layer.removeEntity(bullet);
					}
				});
			}
			baseGameActivity.runOnUpdateThread(new Runnable()
			{
				@Override
				public void run()
				{
					if (player.isDead())
					{
						Area1BossBulletCollidable.this.layer.removeEntity(player);
					}
				}
			});
		}
	}

}
