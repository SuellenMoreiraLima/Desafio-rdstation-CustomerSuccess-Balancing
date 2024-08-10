package com.rdstation;

import java.util.*;

public class CustomerSuccessBalancing {

    private final List<CustomerSuccess> customerSuccessList;
    private final List<Customer> customerList;
    private final List<Integer> unavailableCustomerSuccessIds;

    public CustomerSuccessBalancing(List<CustomerSuccess> customerSuccessList,
                                    List<Customer> customerList,
                                    List<Integer> unavailableCustomerSuccessIds) {
        this.customerSuccessList = customerSuccessList;
        this.customerList = customerList;
        this.unavailableCustomerSuccessIds = unavailableCustomerSuccessIds;
    }

    public int run() {
        TreeMap<Integer, List<Integer>> availableCustomerSuccessMap = filterAndSortCustomerSuccesses();
        return findCustomerSuccessWithMostClients(availableCustomerSuccessMap);
    }

    private TreeMap<Integer, List<Integer>> filterAndSortCustomerSuccesses() {
        Set<Integer> unavailableIds = new HashSet<>(unavailableCustomerSuccessIds);
        TreeMap<Integer, List<Integer>> availableCustomerSuccess  = new TreeMap<>(Collections.reverseOrder());

        for (CustomerSuccess customerSuccess : customerSuccessList) {
            if (!unavailableIds.contains(customerSuccess.getId())) {
                availableCustomerSuccess
                        .putIfAbsent(customerSuccess.getScore(), new ArrayList<>());
                availableCustomerSuccess
                        .get(customerSuccess.getScore())
                        .add(customerSuccess.getId());
            }
        }
        return availableCustomerSuccess;
    }

    private int findCustomerSuccessWithMostClients(TreeMap<Integer, List<Integer>> sortedCustomerSuccessMap) {
        Map<Integer, Integer> customerCountPerCs = new HashMap<>();

        for (Customer customer : customerList) {
            Map.Entry<Integer, List<Integer>> closestCsEntry = sortedCustomerSuccessMap.floorEntry(customer.getScore());

            if (closestCsEntry != null) {
                int csId = closestCsEntry.getValue().get(0);
                customerCountPerCs.put(csId, customerCountPerCs.getOrDefault(csId, 0) + 1);
            }
        }

        return findCsWithMaxClients(customerCountPerCs);
    }

    private int findCsWithMaxClients(Map<Integer, Integer> customerCountPerCs) {
        int maxClients = 0;
        Integer csWithMaxClientsId = null;

        for (Map.Entry<Integer, Integer> entry : customerCountPerCs.entrySet()) {
            if (entry.getValue() > maxClients) {
                maxClients = entry.getValue();
                csWithMaxClientsId = entry.getKey();
            } else if (entry.getValue() == maxClients) {
                csWithMaxClientsId = null; // Há um empate
            }
        }

        return csWithMaxClientsId != null ? csWithMaxClientsId : 0;
    }
}
