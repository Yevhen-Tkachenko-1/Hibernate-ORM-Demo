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

**MandatoryNamedItem** Table:
```sql
CREATE TABLE IF NOT EXISTS public.mandatory_named_items(
    mandatory_named_item_id   serial,
    mandatory_named_item_name varchar(255) NOT NULL,
    PRIMARY KEY (mandatory_named_item_id)
);
```