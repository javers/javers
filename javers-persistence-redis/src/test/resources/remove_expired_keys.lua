-- Define the global set key
local jv_snapshots_keys_set = 'jv_snapshots_keys'

-- Get all members of the global set
local snapshot_keys = redis.call('SMEMBERS', jv_snapshots_keys_set)

-- Iterate over each key in the global set
for _, snapshot_key in ipairs(snapshot_keys) do
    -- Check if the key exists in Redis
    if redis.call('EXISTS', snapshot_key) == 0 then
        -- If the key does not exist, remove it from the global set
        redis.call('SREM', jv_snapshots_keys_set, snapshot_key)

        -- Extract the entity class name from the key pattern
        local entity_class_name = string.match(snapshot_key, "jv_snapshots_keys:(.+)")
        if entity_class_name then
            -- Define the entity-specific set key
            local entity_specific_set = 'jv_snapshots_keys:' .. entity_class_name
            -- Remove the key from the entity-specific set
            redis.call('SREM', entity_specific_set, snapshot_key)
        end
    end
end

-- Return true to indicate successful execution
return true
