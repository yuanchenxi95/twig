import { Tag } from 'proto/api/tag';
import React from 'react';

export function TagCard(props: { tag: Tag }) {
    return <div>{props.tag.name}</div>;
}
