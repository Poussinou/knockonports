/*
 * Copyright (c) 2018 Alexander Yaburov
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package me.impa.knockonports.database.entity

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import me.impa.knockonports.json.PortData

@Entity(tableName = "tbSequence")
data class Sequence(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "_id")
        var id: Long?,
        @ColumnInfo(name = "_name")
        var name: String?,
        @ColumnInfo(name = "_host")
        var host: String?,
        @ColumnInfo(name = "_timeout")
        var timeout: Int?,
        @ColumnInfo(name = "_order")
        var order: Int?,
        @ColumnInfo(name = "_delay")
        var delay: Int?,
        @ColumnInfo(name = "_udp_content")
        var udpContent: String?,
        @ColumnInfo(name = "_application")
        var application: String?,
        @ColumnInfo(name = "_base64")
        var base64: Int?,
        @ColumnInfo(name = "_port_string")
        var portString: String?

) {
    @Ignore
    var selected: Boolean = false

    fun getPortList(): List<PortData> {
        return portString?.split(ENTRY_SEPARATOR)?.map {
            val p = it.split(PORT_SEPARATOR)
            PortData(if (p.isNotEmpty()) {
                p[0].toIntOrNull()
            } else {
                null
            },
                    if (p.size > 1) {
                        p[1].toIntOrNull() ?: PORT_TYPE_UDP
                    } else {
                        PORT_TYPE_UDP
                    })
        }?.toList() ?: listOf()
    }

    fun getReadablePortString(): String? {
        return portString?.split(ENTRY_SEPARATOR)?.map {
            val p = it.split(PORT_SEPARATOR)
            if (p.isNotEmpty()) {
                if (p[0].isNullOrEmpty()){
                    return@map null
                }
                p[0]
            } else {
                return@map null
            } + ":" + if (p.size > 1 && p[1].toIntOrNull() == PORT_TYPE_TCP) {
                "TCP"
            } else {
                "UDP"
            }
        }?.filter { it != null }?.joinToString(", ")
    }

    companion object {
        const val INVALID_SEQ_ID = -100500L
        const val PORT_TYPE_UDP = 0
        const val PORT_TYPE_TCP = 1
        const val ENTRY_SEPARATOR = '|'
        const val PORT_SEPARATOR = ':'

        fun compilePortString(portList: List<PortData>?): String? {
            return portList?.joinToString(ENTRY_SEPARATOR.toString()) { (it.value?.toString() ?: "") + PORT_SEPARATOR + it.type }
        }
    }
}