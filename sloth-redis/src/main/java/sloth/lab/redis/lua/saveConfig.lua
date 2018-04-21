-- 自定义函数，比较两个SET是否相等
local function isSetEqual(set1, set2)
  if (set1 == nil or #set1 == 0) then
    if (set2 == nil or #set2 == 0) then
      return true
    else
      return false
    end
  else
    if (set2 == nil or #set2 == 0) then
      return false
    else
      if (#set1 ~= #set2) then
        return false
      end
    end
  end

  for i1,v1 in pairs(set1) do
    local tempRes = false
    for i2,v2 in pairs(set2) do
      if (v1 == v2) then
        tempRes = true
        break
      end
    end
    if (not(tempRes)) then
      return false
    end
  end

  return true
end

local function sadd(key, set)
  for i,v in pairs(set) do
    redis.call('SADD', key, v)
  end
end

-- 保存模块配置
local moduleVersionUnit = KEYS[1]
local documentVersion = tonumber(KEYS[2])
local document = KEYS[3]
local mdptInSize = tonumber(KEYS[4])
local mdptOutSize = tonumber(KEYS[5])

local mdptIn = {}
if (mdptInSize > 0) then
  for pos=6, 6+mdptInSize-1, 1 do
    table.insert(mdptIn, KEYS[pos])
  end
end
local mdptOut = {}
if (mdptOutSize > 0) then
  for pos=6+mdptInSize, 6+mdptInSize+mdptOutSize-1, 1 do
    table.insert(mdptOut, KEYS[pos])
  end
end

local result = {}

local tmpDocumentVersion = tonumber(redis.call('HGET', 'mdv', moduleVersionUnit))
if (tmpDocumentVersion == nil) then
  redis.call('HSET', 'md', moduleVersionUnit..'_'..documentVersion, document)
  sadd('mdpt_i_'..moduleVersionUnit..'_'..documentVersion, mdptIn)
  sadd('mdpt_o_'..moduleVersionUnit..'_'..documentVersion, mdptOut)
  redis.call('HSET', 'mdv', moduleVersionUnit, documentVersion)
  redis.call('HINCRBY', 'mdpt_o_iv', moduleVersionUnit, 1)
  redis.call('HINCRBY', 'mgv', moduleVersionUnit, 1)
elseif (documentVersion > tmpDocumentVersion) then
  -- update
  redis.call('HSET', 'md', moduleVersionUnit..'_'..documentVersion, document)
  sadd('mdpt_i_'..moduleVersionUnit..'_'..documentVersion, mdptIn)
  sadd('mdpt_o_'..moduleVersionUnit..'_'..documentVersion, mdptOut)
  redis.call('HSET', 'mdv', moduleVersionUnit, documentVersion)

  local tmpDptOut = redis.call('SMEMBERS', 'mdpt_o_'..moduleVersionUnit..'_'..tmpDocumentVersion)
  -- 判断本区域呼出依赖是否有变更
  if (not(isSetEqual(tmpDptOut, mdptOut))) then
    redis.call('HINCRBY', 'mdpt_o_iv', moduleVersionUnit, 1)
  end
  redis.call('HINCRBY', 'mgv', moduleVersionUnit, 1)
  -- 删除原有数据
  redis.call('HDEL', 'md', moduleVersionUnit..'_'..tmpDocumentVersion)
  redis.call('DEL', 'mdpt_i_'..moduleVersionUnit..'_'..tmpDocumentVersion)
  redis.call('DEL', 'mdpt_o_'..moduleVersionUnit..'_'..tmpDocumentVersion)
end

return result
