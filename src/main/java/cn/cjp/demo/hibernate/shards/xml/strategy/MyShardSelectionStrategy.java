package cn.cjp.demo.hibernate.shards.xml.strategy;

import java.math.BigInteger;
import java.util.List;

import org.hibernate.shards.ShardId;
import org.hibernate.shards.strategy.selection.ShardSelectionStrategy;

import cn.cjp.demo.hibernate.shards.xml.entity.ShardableEntity;

/*
 * a simple ShardSelectionStrategy implementation for our ContactEntity
 */
public class MyShardSelectionStrategy implements ShardSelectionStrategy {
	private List<ShardId> _shardIds;

	public MyShardSelectionStrategy(List<ShardId> shardIds) {
		this._shardIds = shardIds;
		System.out.println("shardIds:" + shardIds);
	}

	public ShardId selectShardIdForNewObject2(Object obj) {
		if (obj instanceof ShardableEntity) {
			System.out.println(obj);
			BigInteger id = ((ShardableEntity) obj).getIdentifier();
			int i = id.mod(new BigInteger("2")).intValue();
			System.out.println("int: " + i);
			return new ShardId(i);
		}
		// for non-shardable entities we just use shard0
		throw new IllegalArgumentException("类型错误 "+obj.getClass());
	}

	public ShardId selectShardIdForNewObject(Object obj) {
		if (obj instanceof ShardableEntity) {
			BigInteger id = ((ShardableEntity) obj).getIdentifier();
			if (id == null || id.equals(0))
				return this._shardIds.get(0);

			int i = id.mod(new BigInteger("2")).intValue();
			System.out.println("int: " + i);
			return this._shardIds.get(i);
		}
		// for non-shardable entities we just use shard0
		return this._shardIds.get(0);
	}
}
