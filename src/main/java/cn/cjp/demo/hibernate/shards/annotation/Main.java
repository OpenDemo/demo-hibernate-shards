package cn.cjp.demo.hibernate.shards.annotation;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.shards.ShardId;
import org.hibernate.shards.ShardedConfiguration;
import org.hibernate.shards.cfg.ConfigurationToShardConfigurationAdapter;
import org.hibernate.shards.cfg.ShardConfiguration;
import org.hibernate.shards.strategy.ShardStrategy;
import org.hibernate.shards.strategy.ShardStrategyFactory;
import org.hibernate.shards.strategy.ShardStrategyImpl;
import org.hibernate.shards.strategy.access.SequentialShardAccessStrategy;
import org.hibernate.shards.strategy.access.ShardAccessStrategy;
import org.hibernate.shards.strategy.resolution.ShardResolutionStrategy;
import org.hibernate.shards.strategy.selection.ShardSelectionStrategy;

import cn.cjp.demo.hibernate.shards.xml.entity.ContactEntity;
import cn.cjp.demo.hibernate.shards.xml.strategy.MyShardResolutionStrategy;
import cn.cjp.demo.hibernate.shards.xml.strategy.MyShardSelectionStrategy;


@SuppressWarnings("deprecation")
public class Main {
	public static void main(String[] args) {
		HibernateShardsTest(args);
	}

	private static SessionFactory createSessionFactory() {
		// 加载主配置文件，为每个shard创建SessionFactory对象时将
		// 以他作为原型
		AnnotationConfiguration prototypeCfg = new AnnotationConfiguration()
				.configure("annotation/shard0.hibernate.cfg.xml");
		prototypeCfg.addAnnotatedClass(ContactEntity.class);
		// 每个shard的配置文件
		List<ShardConfiguration> shardCfgs = new ArrayList<ShardConfiguration>();
		shardCfgs.add(buildShardConfig("annotation/shard0.hibernate.cfg.xml"));
		shardCfgs.add(buildShardConfig("annotation/shard1.hibernate.cfg.xml"));
		// 数据切片策略的工厂对象
		ShardStrategyFactory strategyFactory = buildShardStrategyFactory();
		ShardedConfiguration shardedConfig = new ShardedConfiguration(
				prototypeCfg, shardCfgs, strategyFactory);
		// 返回一个ShardedSessionFactory对象
		return shardedConfig.buildShardedSessionFactory();
	}

	private static ShardStrategyFactory buildShardStrategyFactory() {
		ShardStrategyFactory factory = new ShardStrategyFactory() {
			// 测试用的自定义数据切片策略的工厂类
			public ShardStrategy newShardStrategy(List<ShardId> shardIds) {
				ShardSelectionStrategy ss = new MyShardSelectionStrategy(
						shardIds);
				ShardResolutionStrategy rs = new MyShardResolutionStrategy(
						shardIds);
				ShardAccessStrategy as = new SequentialShardAccessStrategy();
				return new ShardStrategyImpl(ss, rs, as);
			}
		};
		return factory;
	}

	private static ShardConfiguration buildShardConfig(String configFile) {
		Configuration config = new Configuration().configure(configFile);
		return new ConfigurationToShardConfigurationAdapter(config);
	}

	private static void HibernateShardsTest(String[] args) {
		String loginId = "root";
		String password = "root";
		if (args != null && args.length == 2) {
			loginId = args[0];
			password = args[1];
		}
		SessionFactory factory = null;
		try {
			factory = createSessionFactory();
			for(int i=0; i<5; i++){
				ShardsTestCreate(factory);
			}
			ShardsTestLogin(factory, loginId, password);
			// ShardsTestDelete(factory);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			if (factory != null)
				factory.close();
		}
	}

	private static void ShardsTestCreate(SessionFactory factory) {
		Session session = null;
		Transaction transaction = null;
		System.out.println("===Create Contacts===");
		try {
			session = factory.openSession();
			transaction = session.beginTransaction();
			session.save(new ContactEntity("RicCC@cnblogs.com",
					"123", "Richie", "RicCC@cnblogs.com"));
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null)
				transaction.rollback();
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			if (session != null)
				session.close();
		}
	}

	@SuppressWarnings("rawtypes")
	private static void ShardsTestLogin(SessionFactory factory, String loginId,
			String password) {
		Session session = null;
		ContactEntity c = null;
		System.out.println("\n===Login Test===");
		try {
			session = factory.openSession();
			List contacts = session
					.createQuery("from ContactEntity where LoginId=:loginId")
					.setString("loginId", loginId).list();
			if (contacts.isEmpty())
				System.out.println("Contact \"" + loginId + "\" not found!");
			else {
				c = (ContactEntity) contacts.get(0);
				if (c.getPassword().equals(password))
					System.out.println("Contact \"" + loginId
							+ "\" login successful");
				else
					System.out.println("Password is incorrect (should be: "
							+ c.getPassword() + ", but is: " + password + ")");
			}

			System.out.println("\n===Get Contact by Id===");
			c = (ContactEntity) session.get(ContactEntity.class, new BigInteger("1"));
			System.out.println(c);
			c = (ContactEntity) session.get(ContactEntity.class, new BigInteger("2"));
			System.out.println(c);
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			if (session != null)
				session.close();
		}
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	private static void ShardsTestDelete(SessionFactory factory) {
		Session session = null;
		Transaction transaction = null;
		System.out.println("\n===Delete Contacts===");
		try {
			session = factory.openSession();
			transaction = session.beginTransaction();
			List contacts = session.createQuery("from ContactEntity").list();
			Iterator it = contacts.iterator();
			while (it.hasNext()) {
				session.delete(it.next());
			}
			transaction.commit();
		} catch (Exception e) {
			if (transaction != null)
				transaction.rollback();
			System.out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			if (session != null)
				session.close();
		}
	}
}