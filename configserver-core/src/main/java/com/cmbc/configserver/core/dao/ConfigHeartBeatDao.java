package com.cmbc.configserver.core.dao;

import java.util.List;

import com.cmbc.configserver.domain.ConfigHeartBeat;

/**
 * Created by tongchuan.lin<linckham@gmail.com><br/>
 *
 * @Date 2014/10/30
 * @Time 15:45
 */
public interface ConfigHeartBeatDao {
    public boolean save(ConfigHeartBeat heartBeat) throws Exception;

    public boolean update(ConfigHeartBeat heartBeat) throws Exception;

    public boolean delete(String clientId) throws Exception;
    
    public ConfigHeartBeat get(String clientId) throws Exception;
    
    public List<ConfigHeartBeat> getTimeout() throws Exception;

}
