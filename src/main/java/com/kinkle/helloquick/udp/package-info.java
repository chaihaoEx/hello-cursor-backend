/**
 * UDP协议服务包
 * 
 * <p>基于Netty的UDP服务实现，支持固定长度包头和可变长度包体的协议设计。</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>协议包头解析和构建</li>
 *   <li>多种编码格式支持（JSON、YAML、XML、Binary）</li>
 *   <li>加密解密支持（无加密、DTLS、AES）</li>
 *   <li>CRC32校验机制</li>
 *   <li>协议路由和消息分发</li>
 * </ul>
 * 
 * @author kinkle
 * @version 1.0
 * @since 2025-08-31
 */
package com.kinkle.helloquick.udp;
