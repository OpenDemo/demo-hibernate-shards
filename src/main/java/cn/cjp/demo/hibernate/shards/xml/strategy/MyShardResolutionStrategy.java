package cn.cjp.demo.hibernate.shards.xml.strategy;

import java.util.List;
import java.util.ArrayList;

import org.hibernate.shards.strategy.resolution.ShardResolutionStrategy;
import org.hibernate.shards.strategy.selection.ShardResolutionStrategyData;
import org.hibernate.shards.ShardId;

/*
 * a simple ShardResolutionStrategy implementation for our ContactEntity
 */
@SuppressWarnings("unchecked")
public class MyShardResolutionStrategy implements ShardResolutionStrategy {
	private List<ShardId> _shardIds;

	public MyShardResolutionStrategy(List<ShardId> shardIds) {
		this._shardIds = shardIds;
		System.out.println("_shardIds: " + _shardIds);
	}

	@SuppressWarnings("rawtypes")
	public List selectShardIdsFromShardResolutionStrategyData(
			ShardResolutionStrategyData arg0) {
		List ids = new ArrayList();
		String id = (String) arg0.getId();
		System.out.println("id: "+id);
		if (id == null || id.isEmpty())
			ids.add(this._shardIds.get(0));
		else {
			// our shard selection is identified by the
			// first char(number) in contact id
			// 0-4 => shards0, 5-9 => shards1
			Integer i = new Integer(id.substring(0, 1));
			System.out.println("shardId: " + this._shardIds);
			ids.add(this._shardIds.get(i / 2));
		}
		return ids;
	}
}
