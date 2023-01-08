package org.assertj.core.condition;

import org.assertj.core.api.Condition;
import org.assertj.core.description.Description;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.condition.NestableCondition.nestable;

public class CollectionsConditions {
    public static <V> Condition<Iterable<? extends V>> contains(Condition<V> conditionOnElement) {
        return new Contains<>(conditionOnElement);
    }

    @SafeVarargs
    public static <V> Condition<List<? extends V>> elementAt(int index, Condition<V>... conditionsOnElement) {
        return nestable(
                String.format("element at %s", index),
                elements -> elements.get(index),
                conditionsOnElement
        );
    }

    private static class Contains<T> extends Join<Iterable<? extends T>> {
        private Contains(Condition<? super T> condition) {
            super(toConditionOnIterable(condition));
        }

        @Override
        public boolean matches(Iterable<? extends T> value) {
            return conditions().stream().allMatch(it -> it.matches(value));
        }

        @Override
        public String descriptionPrefix() {
            return "contains";
        }

        private static <V> Condition<Iterable<? extends V>> toConditionOnIterable(Condition<? super V> condition) {
            var description = condition.description();
            return new Condition<>() {
                @Override
                public boolean matches(Iterable<? extends V> value) {
                    return StreamSupport.stream(value.spliterator(), false).anyMatch(condition::matches);
                }

                @Override
                public Description conditionDescriptionWithStatus(Iterable<? extends V> actual) {
                    return description;
                }
            };
        }
    }
}


