package com.fmtech.fmlite.db;

import java.util.List;

/**
 * ==================================================================
 * Copyright (C) 2018 FMTech All Rights Reserved.
 *
 * @author Drew.Chiang
 * @version v1.0.0
 * @email chiangchuna@gmail.com
 * @description ${todo}
 * <p>
 * ==================================================================
 */

public interface IBaseDao<T> {

    Long insert(T entity);

    int update(T entity, T where);

    int delete(T where);

    List<T> query(T where);

    List<T> query(T where, String orderBy, Integer startIndex, Integer limit);
}
