local snapshot_keys_scan = redis.call('KEYS', 'jv_snapshots:*')
if #snapshot_keys_scan > 0 then
  redis.call('SADD', 'snapshot_keys_scan', unpack(snapshot_keys_scan))
  local snapshot_keys_diff = redis.call('SDIFF', 'jv_snapshots_keys', 'snapshot_keys_scan')
  if #snapshot_keys_diff > 0 then
    redis.call('SREM', 'jv_snapshots_keys', unpack(snapshot_keys_diff))
    local entity_type_name_scan = redis.call('SMEMBERS', 'jv_entity_type_name')
    for _, set_name in ipairs(entity_type_name_scan) do
      redis.debug(set_name)
      redis.call('SREM', set_name, unpack(snapshot_keys_diff))
    end
  end
  redis.call('DEL', 'snapshot_keys_scan')
end