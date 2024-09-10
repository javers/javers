local function processBatches(keys, command, set_name)
  local batch_size = 5000
  for i = 1, #keys, batch_size do
	local batch = {}
	for j = i, math.min(i + batch_size - 1, #keys) do
		table.insert(batch, 0)
		table.insert(batch, keys[j])
	end
	redis.call(command, set_name, unpack(batch))	
  end
end

local snapshot_keys_scan = redis.call('KEYS', 'jv_snapshots:*')
local snapshot_keys_diff

if #snapshot_keys_scan > 0 then
  processBatches(snapshot_keys_scan, 'ZADD', 'snapshot_keys_scan')
  snapshot_keys_diff = redis.call('ZDIFF', 'jv_snapshots_keys', 'snapshot_keys_scan')
else
  snapshot_keys_diff = redis.call('ZRANGE', 'jv_snapshots_keys', 0, -1)
end

local entity_type_name_scan = redis.call('ZRANGE', 'jv_snapshots_keys_set', 0, -1)
for _, set_name in ipairs(entity_type_name_scan) do
  processBatches(snapshot_keys_diff, 'ZREM', set_name)
end

processBatches(snapshot_keys_diff, 'ZREM', 'jv_snapshots_keys')
redis.call('DEL', 'snapshot_keys_scan')
