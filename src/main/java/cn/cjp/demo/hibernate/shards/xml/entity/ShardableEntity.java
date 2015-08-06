package cn.cjp.demo.hibernate.shards.xml.entity;

import java.math.BigInteger;

public interface ShardableEntity {
    public BigInteger getIdentifier();
}
