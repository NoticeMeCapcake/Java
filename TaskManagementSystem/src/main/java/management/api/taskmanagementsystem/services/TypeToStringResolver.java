package management.api.taskmanagementsystem.services;

import management.api.taskmanagementsystem.contracts.SortingType;
import org.springframework.stereotype.Service;

@Service
public class TypeToStringResolver {
    public String resolve(SortingType type) {
        var resolvedType = type.toString().toLowerCase();
        if (type == SortingType.STATE || type == SortingType.PRIORITY) {
            resolvedType += "_id";
        }
        if (type == SortingType.UNSORTED) {
            resolvedType = "";
        }
        return resolvedType;
    }

}
