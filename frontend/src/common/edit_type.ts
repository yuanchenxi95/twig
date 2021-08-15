export enum EditType {
    EDIT = 'EDIT',
    CREATE = 'CREATE',
}

export function getEditType(data: { id: string }) {
    return data.id === '' ? EditType.CREATE : EditType.EDIT;
}

export function getEditTypeDisplayName(editType: EditType) {
    return editType === EditType.EDIT ? 'Edit' : 'Create';
}
