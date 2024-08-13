local function processBatches(keys, command, set_name)
  local batch_size = 5000
  for i = 1, #keys, batch_size do
        redis.call(command, set_name, unpack(keys, i, math.min(i + batch_size - 1, #keys)))
  end
end

local snapshot_keys_scan = redis.call('KEYS', 'jv_snapshots:*')
local snapshot_keys_diff

if #snapshot_keys_scan > 0 then
  processBatches(snapshot_keys_scan, 'SADD', 'snapshot_keys_scan')
  snapshot_keys_diff = redis.call('SDIFF', 'jv_snapshots_keys', 'snapshot_keys_scan')
else
  snapshot_keys_diff = redis.call('SMEMBERS', 'jv_snapshots_keys')
end

local entity_type_name_scan = redis.call('SMEMBERS', 'jv_snapshots_keys_set')
for _, set_name in ipairs(entity_type_name_scan) do
  processBatches(snapshot_keys_diff, 'SREM', set_name)
end

processBatches(snapshot_keys_diff, 'SREM', 'jv_snapshots_keys')
redis.call('DEL', 'snapshot_keys_scan')
