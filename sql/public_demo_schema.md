**SelfIdentifiable** Table:
```sql
CREATE TABLE IF NOT EXISTS public.self_assigned_ids(
    identity_id   int,
    PRIMARY KEY (identity_id)
);
```

**VersionedItem** Table:
```sql
CREATE TABLE IF NOT EXISTS public.versioned_items(
    item_id   serial,
    item_name varchar(255),
    version   int           DEFAULT 0,
    PRIMARY KEY (item_id)
);
```