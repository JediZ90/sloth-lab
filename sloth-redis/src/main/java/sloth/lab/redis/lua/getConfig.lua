local moduleVersionUnit = KEYS[1];
local moduleGlobalVersion = KEYS[2];
local moduleDocumentVersion = KEYS[3];
local moduleDptInstVersion = KEYS[4];
local topologyVersion = KEYS[5]

local result = {};

local tmpGlobalVersion = redis.call('HGET', 'mgv', moduleVersionUnit);

if (tmpGlobalVersion == nil) then
  retult['mgv'] = -1;
elseif (moduleGlobalVersion == nil || tmpGlobalVersion > moduleGlobalVersion) then
  local tmpDocumentVersion = redis.call('HGET', 'mdv', moduleVersionUnit);
  local tmpDptInstVersion = redis.call('HGET', 'mdpt_iv', moduleVersionUnit);
  local tpmTopologyVersion = redis.call('GET','tpv');
  retult['mgv'] = tmpGlobalVersion;
  retult['mdv'] = tmpDocumentVersion;
  retult['mdpt_iv'] = tmpDptInstVersion;
  retult['tpv'] = tpmTopologyVersion;
  if (tmpDocumentVersion > moduleDocumentVersion) then
    retult['md'] = redis.call('HGET', 'md', moduleVersionUnit..'_'..tmpDocumentVersion);
  end
  if (tmpDptInstVersion > moduleDptInstVersion) then
    local tmpDpts = redis.call('SMEMBERS', 'mdpt_o_'..moduleVersionUnit..'_'..tmpDocumentVersion);
    result['mdpt_i'] = "";
  end
  if (tpmTopologyVersion > topologyVersion) then
    retult['tp'] = redis.call('GET', 'tp'..'_'..tpmTopologyVersion);
  end
else
  retult['mgv'] = moduleVersionUnit;
end

return result
