/**
MIT License

Copyright (c) 2018 White Wood City

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package top.gonefuture.vertx.mqtt.config

import scala.reflect.internal.ReporterImpl

/**
 *  公共约定
 */

const val MESSAGE = "message"
const val SEARCH = "search"
const val FRIEND = "friend"
const val MESSAGES = "messages"
const val FRIENDS = "friends"
const val USER = "user"
const val REQUEST = "request"
const val RESPONSE = "response"
const val DELETE = "delete"
const val LOGIN = "login"
const val REGISTER = "register"
const val HISTORY  ="history"
const val TYPE = "type"
const val SUBTYPE = "subtype"
const val VERSION = "version"
const val FROM = "from"
const val TO = "to"
const val INFO = "info"
const val NICKNAME = "nickname"
const val ID = "id"
const val PASSWORD = "password"
const val DIR = "dir"
const val HOST = "host"
const val BODY = "body"
const val ACCEPT = "accept"
const val TEXT = "text"
const val TCP_PORT = "tcp-port"
const val HTTP_PORT = "http-port"
const val HTTPS_PORT = "https-port"
const val KEYWORD = "keyword"
const val OFFLINE = "offline"
const val DATE = "date"
const val TIME = "time"
const val UUID = "uuid"

const val END = "\r\n"
const val SESSION_ID = "social-vertex-id"




// 数据库相关




const val DB_NAME = "iot"
const val DB_PORT = 27017


const val COLLECTION_IOT = "iot_data"
const val IOT_ADD = "/api/iot/add"
const val IOT_FIND = "/api/iot/find"
const val IOT_FIND_ONE = "/api/iot/findone"
const val IOT_COUNT = "/api/iot/count"
const val COMMAND_IOT_UPDATE = "/api/iot/update"
const val IOT_MESSAGE = "/api/iot/message"
const val IOT_CHART_DATA = "/api/iot/chart"

const val COLLECTION_USER = "iot_user"
const val USER_ADD = "/api/user/add"
const val USER_ONE_FIND = "/api/user/find"
const val USER_FIND = "/api/users"
const val USER_DELETE = "/api/user/delete"

const val COLLECTION_WARNING = "iot_warning"
const val WARNING_ADD = "/api/warning/add"
const val WARNING_FIND = "/api/warning/find"
const val WARNING_DELETE = "/api/warning/delete"


const val AREA_FIND = "/api/area/find"
const val DEVICES_FIND = "/api/devices/find"

const val REDIS_CLIENT = "redis_client"

const val TOPICS = "topics"
const val DEVICES= "devices"
const val AREA = "area"
