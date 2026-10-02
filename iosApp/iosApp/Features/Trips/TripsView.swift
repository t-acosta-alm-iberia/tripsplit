import SwiftUI
import SharedLogic

struct TripsView: View {
    let trips: [TripListItem]
    let onTripSelected: (String) -> Void
    let onAddTrip: () -> Void
    var isLoading = false
    var errorMessage: String? = nil
    var onRetry: () -> Void = {}
    
    var body: some View {
        List {
            Section {
                ForEach(trips, id: \.id) { trip in
                    Button {
                        onTripSelected(trip.id)
                    } label : {
                        HStack {
                            Text(trip.name)
                                .foregroundStyle(.primary)
                            
                            Spacer()
                            
                            Image(systemName: "chevron.right")
                                .font(.footnote.weight(.semibold))
                                .foregroundStyle(.tertiary)
                        }.padding(.vertical, 8)
                    }
                    .buttonStyle(.plain)
                }
            }
        }
        .listStyle(.insetGrouped)
        .contentMargins(.top, 24, for: .scrollContent)
        .navigationTitle("Trips")
        .toolbar {
            ToolbarItem(placement: .topBarTrailing) {
                Button(action: onAddTrip) {
                    Label("Add trip", systemImage: "plus")
                }
            }
        }.overlay {
            if isLoading && trips.isEmpty {
                ProgressView("Loading trips…")
            } else if let errorMessage {
                ContentUnavailableView {
                    Label("Could not load trips", systemImage: "exclamationmark.triangle")
                } description: {
                    Text(errorMessage)
                } actions: {
                    Button("Try again", action: onRetry)
                }
            } else if trips.isEmpty {
                ContentUnavailableView(
                    "No trips yet",
                    systemImage: "suitcase",
                    description: Text("Tap + to add your first trip.")
                )
            }
        }
    }
}

#Preview {
    NavigationStack {
        TripsView(
            trips: [
                TripListItem(id: "lisbon", name: "Lisbon weekend"),
                TripListItem(id: "berlin", name: "Berlin with friends")
            ],
            onTripSelected: { _ in },
            onAddTrip: {}
        )
    }
}
